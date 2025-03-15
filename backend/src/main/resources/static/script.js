document.addEventListener('DOMContentLoaded', function() {
    // Update food saved count on page load
    updateFoodSaved();

    // Handle form submission
    document.querySelector('.generateMeal').addEventListener('click', function(e) {
        e.preventDefault();
        console.log('Generate Meal button clicked');
        
        // Get ingredients from textarea
        const ingredientsText = document.querySelector('#input-text').value.trim();
        console.log('Raw ingredients text:', ingredientsText);
        
        // Split ingredients by commas and clean up whitespace
        const ingredients = ingredientsText.split(',')
            .map(ingredient => ingredient.trim())
            .filter(ingredient => ingredient.length > 0);
        console.log('Processed ingredients:', ingredients);

        if (ingredients.length === 0) {
            alert('Please enter at least one ingredient');
            return;
        }

        // Make API call to get recipe
        console.log('Making API call with ingredients:', ingredients);
        fetch('/api/recipes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ ingredients: ingredients })
        })
        .then(response => {
            console.log('API Response status:', response.status);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(recipe => {
            console.log('Received recipe:', recipe);
            if (!recipe) {
                throw new Error('No recipe found');
            }
            // Store recipe in localStorage for recipe.html
            localStorage.setItem('currentRecipe', JSON.stringify(recipe));
            
            // Navigate to recipe page
            window.location.href = 'recipe.html';
        })
        .catch(error => {
            console.error('Error:', error);
            alert('No recipe found for these ingredients. Please try different ingredients.');
        });
    });
});

function updateFoodSaved() {
    fetch('/api/food-saved')
        .then(response => response.json())
        .then(pounds => {
            document.querySelector('#food-saved').textContent = pounds.toFixed(1);
        })
        .catch(error => {
            console.error('Error fetching food saved:', error);
        });
}
