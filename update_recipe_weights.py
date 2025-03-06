import pandas as pd
import re
from typing import List, Dict, Tuple
import json
import csv
    
def get_ingredient_base_weight(ingredient: str) -> float:
    """Calculate base weight for a single ingredient."""
    # Common ingredient weights in pounds
    weights = {
        'chicken': {
            'whole': 4.0,
            'breast': 0.5,
            'thigh': 0.375,
            'wing': 0.25
        },
        'beef': {
            'ground': 1.0,
            'steak': 0.75,
            'roast': 3.0
        },
        'pork': {
            'chop': 0.5,
            'tenderloin': 1.0,
            'shoulder': 3.0
        },
        'vegetables': {
            'onion': 0.5,
            'potato': 0.375,
            'carrot': 0.25,
            'squash': 1.5,
            'tomato': 0.375,
            'pepper': 0.25,
            'garlic': 0.0625
        },
        'fruits': {
            'apple': 0.375,
            'orange': 0.375,
            'lemon': 0.25,
            'banana': 0.375
        }
    }
    
    # Convert measurements to pounds
    measurement_conversions = {
        'cup': 0.5,  # Average weight of a cup of ingredients in pounds
        'tablespoon': 0.0625,
        'teaspoon': 0.0208,
        'ounce': 0.0625,
        'pound': 1.0,
        'gram': 0.0022
    }
    
    ingredient_lower = str(ingredient).lower()
    
    # Extract quantity and unit
    quantity_pattern = r'(\d+(?:/\d+)?|\d*\.\d+)\s*(cup|tablespoon|teaspoon|pound|ounce|lb|oz|g)'
    matches = re.findall(quantity_pattern, ingredient_lower)
    
    if matches:
        quantity_str, unit = matches[0]
        # Convert fractions to decimals
        if '/' in quantity_str:
            num, denom = map(float, quantity_str.split('/'))
            quantity = num / denom
        else:
            quantity = float(quantity_str)
            
        # Standardize units
        unit_mapping = {
            'lb': 'pound',
            'oz': 'ounce',
            'g': 'gram',
            'tbsp': 'tablespoon',
            'tsp': 'teaspoon'
        }
        unit = unit_mapping.get(unit, unit)
        
        return quantity * measurement_conversions.get(unit, 1.0)
    
    # If no specific measurement, estimate based on ingredient type
    for category, items in weights.items():
        for item, weight in items.items():
            if item in ingredient_lower:
                return weight
    
    # Default weight for unknown ingredients
    return 0.25  # Quarter pound default

def classify_recipe_type(title: str, instructions: str) -> str:
    """Determine recipe type based on title and instructions."""
    keywords = {
        'soup': ['soup', 'broth', 'chowder'],
        'stew': ['stew', 'braised', 'cassoulet'],
        'roast': ['roast', 'roasted', 'baked'],
        'fried': ['fried', 'sautéed', 'pan-fried'],
        'salad': ['salad', 'slaw', 'fresh'],
        'cocktail': ['cocktail', 'drink', 'beverage'],
        'dessert': ['cake', 'pie', 'cookie', 'dessert', 'ice cream'],
        'pasta': ['pasta', 'noodle', 'spaghetti', 'macaroni'],
        'sandwich': ['sandwich', 'burger', 'wrap'],
        'breakfast': ['breakfast', 'pancake', 'waffle', 'eggs']
    }
    
    # Convert to string and handle None values
    title = str(title) if title is not None else ""
    instructions = str(instructions) if instructions is not None else ""
    
    title_lower = title.lower()
    instructions_lower = instructions.lower()
    
    for recipe_type, words in keywords.items():
        if any(word in title_lower for word in words) or \
           any(word in instructions_lower for word in words):
            return recipe_type
    return 'other'

def estimate_recipe_weight(ingredients: List[str], recipe_type: str) -> float:
    """Calculate total recipe weight based on ingredients and type."""
    base_weight = sum(get_ingredient_base_weight(ingredient) for ingredient in ingredients)
    
    # Cooking method modifiers
    type_modifiers = {
        'soup': 1.2,      # accounts for added water
        'stew': 1.1,      # accounts for reduced liquid
        'roast': 0.85,    # accounts for moisture loss
        'baked': 0.9,     # accounts for moisture loss
        'fried': 0.8,     # accounts for oil absorption and moisture loss
        'salad': 1.0,     # no modification
        'cocktail': 0.5,  # standard drink weight
        'dessert': 0.85,  # accounts for cooking loss
        'pasta': 1.8,     # accounts for water absorption
        'sandwich': 1.0,  # no modification
        'breakfast': 0.9  # slight moisture loss
    }
    
    # Apply modifier based on recipe type
    modified_weight = base_weight * type_modifiers.get(recipe_type, 1.0)
    
    # Set minimum and maximum reasonable weights
    min_weight = 0.25  # minimum 4 oz
    max_weight = 10.0  # maximum 10 lbs
    
    return max(min(modified_weight, max_weight), min_weight)

def update_recipe_weights(input_file: str, output_file: str):
    """Update recipe weights in CSV file."""
    try:
        # Read the CSV file
        df = pd.read_csv(input_file)
        print(f"Successfully loaded {len(df)} recipes.")
        
        # Initialize counters
        updated_count = 0
        zero_weight_count = 0
        
        # Process each recipe
        for index, row in df.iterrows():
            try:
                if pd.isna(row['Estimated_Pounds']) or row['Estimated_Pounds'] == 0.0:
                    # Convert string representation of list to actual list
                    ingredients = eval(row['Cleaned_Ingredients'])
                    recipe_type = classify_recipe_type(row['Title'], row['Instructions'])
                    
                    # Calculate new weight
                    new_weight = estimate_recipe_weight(ingredients, recipe_type)
                    
                    # Update the dataframe
                    df.at[index, 'Estimated_Pounds'] = new_weight
                    updated_count += 1
                    
                    if row['Estimated_Pounds'] == 0.0:
                        zero_weight_count += 1
                    
                    # Print progress every 100 recipes
                    if updated_count % 100 == 0:
                        print(f"Processed {updated_count} recipes...")
            except Exception as e:
                print(f"Error processing recipe at index {index}: {str(e)}")
                continue
        
        # Save the updated CSV
        df.to_csv(output_file, index=False)
        print(f"\nProcessing complete!")
        print(f"Updated {updated_count} recipes")
        print(f"Fixed {zero_weight_count} zero-weight recipes")
        print(f"Results saved to {output_file}")
        
    except Exception as e:
        print(f"Error processing file: {str(e)}")

def csv_to_json(input_file: str, output_file: str):
    """Convert CSV file to JSON file with proper formatting."""
    try:
        # Read CSV file
        df = pd.read_csv(input_file)
        
        # Convert DataFrame to list of dictionaries
        recipes = df.to_dict('records')
        
        # Write to JSON file with proper formatting
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump({
                "recipes": recipes
            }, f, indent=2, ensure_ascii=False)
        
        print(f"Successfully converted CSV to JSON and saved to {output_file}")
    except Exception as e:
        print(f"Error converting CSV to JSON: {str(e)}")

def format_json_file(input_file: str, output_file: str):
    """Format an existing JSON file properly."""
    try:
        # Read the existing JSON file
        with open(input_file, 'r', encoding='utf-8') as f:
            # Read lines and join them
            content = f.read()
            # Try to parse as a single JSON object first
            try:
                data = json.loads(content)
            except json.JSONDecodeError:
                # If that fails, try to parse as line-delimited JSON
                lines = content.strip().split('\n')
                data = [json.loads(line) for line in lines if line.strip()]
        
        # Write formatted JSON
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump({
                "recipes": data if isinstance(data, list) else [data]
            }, f, indent=2, ensure_ascii=False)
        
        print(f"Successfully formatted JSON and saved to {output_file}")
    except Exception as e:
        print(f"Error formatting JSON: {str(e)}")

if __name__ == "__main__":
    input_file = "Final_Updated_Recipe_Data.csv"
    output_csv = "Final_Updated_Recipe_Data_with_weights.csv"
    output_json = "Final_Updated_Recipe_Data_with_weights.json"
    
    # Update weights and save to CSV
    update_recipe_weights(input_file, output_csv)
    
    # # Convert to properly formatted JSON
    # csv_to_json(output_csv, output_json)
    
    # If you already have a JSON file that needs formatting:
    format_json_file("Final_Updated_Recipe_Data_with_weights.json", "Final_Updated_Recipe_Data_with_weights.json")