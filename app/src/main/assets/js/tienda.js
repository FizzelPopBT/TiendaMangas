document.addEventListener("DOMContentLoaded", () => {
  const contenedor = document.getElementById("contenedor-productos");
  const productos = JSON.parse(localStorage.getItem("productos")) || [];

  // Mostrar productos
  function mostrarProductos(lista) {
    contenedor.innerHTML = "";

    if (lista.length === 0) {
      contenedor.innerHTML = `<p style="color:#ccc;">No hay productos disponibles.</p>`;
      return;
    }

    lista.forEach((p, i) => {
      const card = document.createElement("div");
      card.classList.add("producto");
      card.innerHTML = `
        <img src="${p.imagen}" alt="${p.nombre}">
        <h3>${p.nombre}</h3>
        <p><strong>$${parseInt(p.precio).toLocaleString("es-CL")}</strong></p>
        <p class="categoria">${p.categoria}</p>
        <button class="add-to-cart" data-index="${i}">
          <i class="fas fa-cart-plus"></i> Agregar
        </button>
      `;
      contenedor.appendChild(card);
    });

    // Vincular botones de "Agregar"
    const botones = document.querySelectorAll(".add-to-cart");
    botones.forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = e.currentTarget.getAttribute("data-index");
        agregarAlCarrito(productos[index]);
      });
    });
  }

  mostrarProductos(productos);

  // 🔍 Búsqueda
  const inputBusqueda = document.getElementById("busqueda");
  if (inputBusqueda) {
    inputBusqueda.addEventListener("input", () => {
      const texto = inputBusqueda.value.toLowerCase();
      const filtrados = productos.filter(
        (p) =>
          p.nombre.toLowerCase().includes(texto) ||
          p.categoria.toLowerCase().includes(texto)
      );
      mostrarProductos(filtrados);
    });
  }

  // 🛒 Agregar al carrito
  function agregarAlCarrito(producto) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    carrito.push(producto);
    localStorage.setItem("carrito", JSON.stringify(carrito));

    // Contador de carrito
    actualizarContadorCarrito();
    alert(`🛍️ "${producto.nombre}" se agregó al carrito.`);
  }

  // 🔢 Actualiza contador en el ícono
  function actualizarContadorCarrito() {
    const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    const contador = document.getElementById("cart-count");
    if (contador) contador.textContent = carrito.length;
  }

  actualizarContadorCarrito();
});