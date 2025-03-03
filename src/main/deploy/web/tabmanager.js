function showContainer(containerClass) {
    document.querySelectorAll('.container').forEach(c => c.classList.remove('active'));
    document.querySelector(`.${containerClass}`).classList.add('active');

    document.querySelectorAll('.tab-button').forEach(b => b.classList.remove('active'));
    document.querySelector(`.tab-button[onclick*="${containerClass}"]`).classList.add('active');
}