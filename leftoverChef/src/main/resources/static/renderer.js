//sample code for a button
// javascript drag functionality api https://www.w3schools.com/html/html5_draganddrop.asp
let foodSaved = 0;


document.getElementById('Generate Meal').addEventListener('click', () => {
  foodSaved++;
  document.getElementById('Food Saved: ').innerText = `Food Saved: ${foodSaved}`;
});

// document.getElementById('imageUpload').addEventListener('change', function(event) {
//     const file = event.target.files[0];
//     if (file) {
//         const reader = new FileReader();
//         reader.onload = function(e) {
//             const img = document.getElementById('uploadedImage');
//             img.src = e.target.result;
//             img.style.display = 'block';
//         };
//         reader.readAsDataURL(file);
//     }
// });
/*
document.getElementById('imageUpload').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            createMagnet(e.target.result);
        };
        reader.readAsDataURL(file);
    }
});
*/

function createMagnet(imageSrc) {
    const img = document.createElement('img');
    img.src = imageSrc;
    img.classList.add('magnet');
    img.style.top = `${Math.random() * 500}px`; 
    img.style.left = `${Math.random() * 300}px`; 
    fridge.appendChild(img);

    img.addEventListener('mousedown', startDrag);
}

function addPresetMagnet() {
    const default image = 'white.jpg'
    createMagnet(randomImg);
}

// drag functionality sort of (api didn't work)
let selectedMagnet = null;
let offsetX = 0, offsetY = 0;

function buttonFuntion(){
    
}