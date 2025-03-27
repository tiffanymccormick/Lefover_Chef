document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.ingredients-box');
    const textarea = document.getElementById('input-text');
    const generateButton = document.querySelector('.generateMeal');
    let currentRecipe = null;
    let excludedRecipes = [];

    generateButton.addEventListener('click', function(e) {
        e.preventDefault();
        const ingredients = textarea.value.split(',').map(i => i.trim()).filter(i => i);
        
        if (ingredients.length === 0) {
            alert('Please enter some ingredients');
            return;
        }

        // Make API call to search for recipes
        fetch(`/api/recipes/search?${ingredients.map(i => `ingredients=${encodeURIComponent(i)}`).join('&')}${excludedRecipes.map(id => `&excludeIds=${id}`).join('')}`)
            .then(response => response.json())
            .then(recipes => {
                if (recipes && recipes.length > 0) {
                    // Store all matched recipes
                    localStorage.setItem('matchedRecipes', JSON.stringify(recipes));
                    localStorage.setItem('currentIndex', '0');
                    
                    // Store and display first recipe
                    currentRecipe = recipes[0];
                    displayRecipe(currentRecipe);
                    
                    if (currentRecipe.id) {
                        excludedRecipes.push(currentRecipe.id);
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
        // Store current recipe data in localStorage for the recipe page
        localStorage.setItem('currentRecipe', JSON.stringify(recipe));
        // Redirect to recipe page
        window.location.href = 'recipe.html';
    }
});
