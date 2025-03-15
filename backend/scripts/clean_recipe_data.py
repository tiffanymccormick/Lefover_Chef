import json
import sys
from pathlib import Path

def clean_recipe(recipe):
    """Clean and standardize a single recipe object."""
    cleaned = {}
    
    # Standardize field names and clean values
    field_mapping = {
        'Recipe Index': 'recipeIndex',
        'Title': 'title',
        'Instructions': 'instructions',
        'Image_Name': 'imageName',
        'Estimated_Time_Minutes': 'estimatedTimeMinutes',
        'Ingredients': 'ingredients',
        'Cleaned_Ingredients': 'cleanedIngredients',
        'Estimated_Pounds': 'estimatedPounds'
    }
    
    for old_key, new_key in field_mapping.items():
        if old_key in recipe:
            value = recipe[old_key]
            # Clean string values
            if isinstance(value, str):
                value = value.strip()
            # Parse cleaned ingredients if it's a string representation of a list
            if old_key == 'Cleaned_Ingredients' and isinstance(value, str):
                try:
                    if value.startswith('[') and value.endswith(']'):
                        # Remove brackets and split by comma
                        ingredients = value[1:-1].split(',')
                        # Clean each ingredient
                        value = [ing.strip().strip('"\'') for ing in ingredients if ing.strip()]
                except Exception as e:
                    print(f"Error parsing cleaned ingredients: {e}")
                    value = []
            cleaned[new_key] = value
    
    return cleaned

def clean_recipe_data(input_file, output_file):
    """Clean and standardize the recipe data JSON file."""
    try:
        # Read the input file
        with open(input_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Handle both array and object formats
        if isinstance(data, dict) and 'recipes' in data:
            recipes = data['recipes']
        elif isinstance(data, list):
            recipes = data
        else:
            raise ValueError("Invalid data format: expected list or object with 'recipes' key")
        
        # Clean each recipe
        cleaned_recipes = []
        for recipe in recipes:
            try:
                cleaned_recipe = clean_recipe(recipe)
                if cleaned_recipe:
                    cleaned_recipes.append(cleaned_recipe)
            except Exception as e:
                print(f"Error cleaning recipe: {e}")
                continue
        
        # Create output directory if it doesn't exist
        output_path = Path(output_file)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        
        # Write the cleaned data
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump({'recipes': cleaned_recipes}, f, indent=2, ensure_ascii=False)
        
        print(f"Successfully cleaned {len(cleaned_recipes)} recipes")
        print(f"Cleaned data saved to: {output_file}")
        
    except Exception as e:
        print(f"Error processing file: {e}")
        sys.exit(1)

if __name__ == '__main__':
    input_file = '../src/main/resources/Final_Updated_Recipe_Data_with_weights.json'
    output_file = '../src/main/resources/cleaned_recipe_data.json'
    
    # Get absolute paths
    script_dir = Path(__file__).parent
    input_path = (script_dir / input_file).resolve()
    output_path = (script_dir / output_file).resolve()
    
    print(f"Cleaning recipe data...")
    print(f"Input file: {input_path}")
    print(f"Output file: {output_path}")
    
    clean_recipe_data(input_path, output_path)
