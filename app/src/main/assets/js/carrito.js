document.addEventListener("DOMContentLoaded", () => {
  const contenedor = document.getElementById("lista-carrito");
  const totalElemento = document.getElementById("total");
  const vaciarBtn = document.getElementById("vaciarCarrito");
  const finalizarBtn = document.getElementById("finalizarCompra");

  let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

  // 🟣 Renderizar carrito
  function renderCarrito() {
    if (!contenedor || !totalElemento) return;

    contenedor.innerHTML = "";

    if (carrito.length === 0) {
      contenedor.innerHTML = `<p style="color:#ccc;">Tu carrito está vacío 🛒</p>`;
      totalElemento.textContent = "$0";
      return;
    }

    carrito.forEach((p, i) => {
      const item = document.createElement("div");
      item.classList.add("item-carrito");
      item.innerHTML = `
        <img src="${p.imagen}" alt="${p.nombre}">
        <div class="info">
          <h4>${p.nombre}</h4>
          <p>$${parseInt(p.precio).toLocaleString("es-CL")}</p>
        </div>
        <button class="eliminar" data-index="${i}">
          <i class="fas fa-trash-alt"></i>
        </button>
      `;
      contenedor.appendChild(item);
    });

    actualizarTotal();
    asignarEventosEliminar();
  }

  // 🧾 Calcular total
  function actualizarTotal() {
    const total = carrito.reduce((acc, p) => acc + parseInt(p.precio), 0);
    totalElemento.textContent = `$${total.toLocaleString("es-CL")}`;
  }

  // ❌ Eliminar producto individual
  function asignarEventosEliminar() {
    const botones = document.querySelectorAll(".eliminar");
    botones.forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = e.currentTarget.getAttribute("data-index");
        carrito.splice(index, 1);
        localStorage.setItem("carrito", JSON.stringify(carrito));
        renderCarrito();
        actualizarContadorCarrito();
      });
    });
  }

  // 🧹 Vaciar carrito
  if (vaciarBtn) {
    vaciarBtn.addEventListener("click", () => {
      if (carrito.length === 0) return;
      const confirmacion = confirm("¿Deseas vaciar el carrito?");
      if (confirmacion) {
        carrito = [];
        localStorage.removeItem("carrito");
        renderCarrito();
        actualizarContadorCarrito();
      }
    });
  }

  // 🟢 Finalizar compra (solo usuarios registrados)
  if (finalizarBtn) {
    finalizarBtn.addEventListener("click", () => {
      const usuarioActivo = JSON.parse(localStorage.getItem("usuarioActivo"));

      if (!usuarioActivo) {
        alert("⚠️ Debes iniciar sesión para hacer un pedido.");
        window.location.href = "login.html";
        return;
      }

      if (carrito.length === 0) {
        alert("🛒 Tu carrito está vacío.");
        return;
      }

      // Crear pedido
      const pedido = {
        id: Date.now(),
        usuario: {
          nombre: usuarioActivo.nombre,
          correo: usuarioActivo.correo,
          usuario: usuarioActivo.usuario,
        },
        productos: carrito,
        total: carrito.reduce((acc, p) => acc + parseInt(p.precio), 0),
        fecha: new Date().toLocaleString(),
      };

      const pedidos = JSON.parse(localStorage.getItem("pedidos")) || [];
      pedidos.push(pedido);
      localStorage.setItem("pedidos", JSON.stringify(pedidos));

      alert("✅ Pedido enviado correctamente. ¡Gracias por tu compra!");

      carrito = [];
      localStorage.removeItem("carrito");
      renderCarrito();
      actualizarContadorCarrito();
    });
  }

  // 🔢 Actualiza contador global
  function actualizarContadorCarrito() {
    const contador = document.getElementById("cart-count");
    if (contador) contador.textContent = carrito.length;
  }

  renderCarrito();
  actualizarContadorCarrito();
});