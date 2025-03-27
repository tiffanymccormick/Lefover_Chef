document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.ingredients-box');
    const textarea = document.getElementById('input-text');
    const generateButton = document.querySelector('.generateMeal');
    let currentRecipe = null;
    let excludedRecipes = JSON.parse(localStorage.getItem('excludedRecipes')) || [];

    generateButton.addEventListener('click', function(e) {
        e.preventDefault();
        const ingredients = textarea.value
            .split(',')
            .map(i => i.trim())
            .filter(i => i)
            .map(i => i.toLowerCase()); // Normalize to lowercase
        
        if (ingredients.length === 0) {
            alert('Please enter some ingredients');
            return;
        }

        // Store ingredients for reuse
        localStorage.setItem('currentIngredients', JSON.stringify(ingredients));
        localStorage.setItem('excludedRecipes', JSON.stringify([]));

        // Make API call to search for recipes
        const queryString = ingredients
            .map(i => `ingredients=${encodeURIComponent(i)}`)
            .join('&');
        
        fetch(`/api/recipes/search?${queryString}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(recipes => {
                if (recipes && recipes.length > 0) {
                    currentRecipe = recipes[0];
                    displayRecipe(currentRecipe);
                    if (currentRecipe.id) {
                        excludedRecipes.push(currentRecipe.id);
                        localStorage.setItem('excludedRecipes', JSON.stringify(excludedRecipes));
                    }
                } else {
                    alert('No matching recipes found for these ingredients');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error finding recipes. Please try again.');
            });
    });

    function displayRecipe(recipe) {
        // Store recipe data in localStorage for the recipe page
        localStorage.setItem('currentRecipe', JSON.stringify(recipe));
        // Redirect to recipe page
        window.location.href = 'recipe.html';
    }
});
