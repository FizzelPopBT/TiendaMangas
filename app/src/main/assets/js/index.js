document.addEventListener("DOMContentLoaded", () => {
  const grid = document.querySelector(".productos .grid");
  const productos = JSON.parse(localStorage.getItem("productos")) || [];

  const destacados = productos.filter((p) => p.destacado && p.stock);

  if (!grid) return;

  if (destacados.length === 0) {
    grid.innerHTML = `<p style="color:#ccc; text-align:center;">No hay lanzamientos destacados por ahora.</p>`;
    return;
  }

  destacados.forEach((p) => {
    const card = document.createElement("div");
    card.classList.add("producto-card");
    card.innerHTML = `
      <div class="producto-img">
        <img src="${p.imagen}" alt="${p.nombre}">
      </div>
      <div class="producto-info">
        <h3>${p.nombre}</h3>
        <p class="categoria">${p.categoria}</p>
        <p class="precio">$${parseInt(p.precio).toLocaleString("es-CL")}</p>
        <a href="tienda.html" class="btn-ver">Ver producto</a>
      </div>
    `;
    grid.appendChild(card);
  });
});