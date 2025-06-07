///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (ARTICULOS Y EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(articulosVenta.length > 0){
        articulosVenta.forEach(art => agregarArticuloExistente(art));
    }
    if(ejemplaresVenta.length > 0){
        ejemplaresVenta.forEach(eje => agregarEjemplarExistente(eje));
    }
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CONTADOR DE LISTA ARTICULOS Y EJEMPLARES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contArticulo = 0;
let contEjemplar = 0;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREAR - ARTICULO Y EJEMPLAR
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarArticulo() {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(item => {
        opciones += `<option value="${item.id}" data-precio="${item.precio}">
                        ${item.presentacion} ${item.nombre}  ($${item.precio})
                    </option>`;
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required>
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].cantidad" class="form-control" min="1" 
                required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].subtotal" class="form-control"
                readonly required>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarArticulo(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
     
    tbody.appendChild(row);
    contArticulo++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarEjemplar() {
    const tbody = document.getElementById('ejemplaresContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(item => {
        opciones += `<option value="${item.id}">
                        ${item.monta.macho.nombre} - ${item.monta.hembra.nombre} (${item.fecha_nacimiento})
                    </option>`;
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <div class="d-flex flex-column align-items-center">
                <input type="file" name="ejemplares[${contEjemplar}].imagen" class="form-control" accept="image/*" 
                    required onchange="mostrarVistaPrevia(this)">

                <img class="img-thumbnail vista-previa" 
                    style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
            </div>
        </td>
        <td>
            <select name="ejemplares[${contEjemplar}].sexo" class="form-select">
                <option value="">Selecciona una opción</option>
                <option value="Macho">Macho</option>
                <option value="Hembra">Hembra</option>
            </select>
        </td>
        <td>
            <input type="number" name="ejemplaresVenta[${contEjemplar}].precio" class="form-control" min="0" 
                required oninput="actualizarTotalVenta()">
        </td>
        <td>
            <select name="ejemplares[${contEjemplar}].nacimiento.id" class="form-select" required>
                ${opciones}
            </select>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contEjemplar++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//UPDATE - ARTICULO EXISTENTE Y EJEMPLAR EXISTENTE
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarArticuloExistente(art) {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(item => {
        // Articulo seleccionado
        const selected = item.id === art.articulo.id ? 'selected' : '';

        opciones += `<option value="${item.id}" data-precio="${item.precio}" ${selected}>
                        ${item.presentacion} ${item.nombre}  ($${item.precio})
                    </option>`;
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <!-- Campo oculto para el id del articulo -->
        <input type="hidden" name="articulosVenta[${contArticulo}].id" value="${art.id}"/>

        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required>
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].cantidad" class="form-control" value="${art.cantidad}" min="1" 
                required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulosVenta[${contArticulo}].subtotal" class="form-control" value="${art.subtotal}" 
                readonly required>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarArticulo(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contArticulo++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarEjemplarExistente(eje) {
    const tbody = document.getElementById('ejemplaresContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(item => {
        const selected = item.id === eje.nacimiento.id ? 'selected' : '';
        opciones += `<option value="${item.id}" ${selected}>
                        ${item.monta.macho.nombre} - ${item.monta.hembra.nombre} (${item.fecha_nacimiento})
                    </option>`;
    });

    //<a href="/img/ejemplares/${eje.nombreImagen}" target="_blank"></a>

    const row = document.createElement('tr');
    row.innerHTML = `
        <!-- Campo oculto para el id del ejemplar -->
        <input type="hidden" name="ejemplares[${contEjemplar}].id" value="${eje.id}"/>
        <input type="hidden" name="ejemplares[${contEjemplar}].nombreImagen" value="${eje.nombreImagen}"/>

        <td>
            <div class="d-flex flex-column align-items-center">
                <input type="file" name="ejemplares[${contEjemplar}].imagen" class="form-control" accept="image/*" 
                    onchange="mostrarVistaPrevia(this)">

                <img class="img-thumbnail vista-previa" 
                    src="/img/ejemplares/${eje.nombreImagen}" 
                    style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
            </div>
        </td>
        <td>
            <select name="ejemplares[${contEjemplar}].sexo" class="form-select">
                <option value="">Selecciona una opción</option>
                <option value="Macho" ${eje.sexo === 'Macho' ? 'selected' : ''}>Macho</option>
                <option value="Hembra" ${eje.sexo === 'Hembra' ? 'selected' : ''}>Hembra</option>
            </select>
        </td>
        <td>
            <input type="number" name="ejemplares[${contEjemplar}].precio" class="form-control" value="${eje.precio}" 
                min="0" required oninput="actualizarTotalVenta()">
        </td>
        <td>
            <select name="ejemplares[${contEjemplar}].nacimiento.id" class="form-select" required>
                ${opciones}
            </select>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contEjemplar++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VISTA PREVIA (IMG)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function mostrarVistaPrevia(input) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            // Busca la celda siguiente y encuentra la imagen
            const img = input.closest('tr').querySelector('.vista-previa');
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//REGISTRAR ARTICULOS Y EJEMPLARES ELIMINADOS (bien)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let articulosEliminados = [];
let ejemplaresEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ELIMINAR ARTICULO Y EJEMPLAR (bien)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function eliminarArticulo(boton) {
    const row = boton.closest('tr'); // Encontramos la fila del artículo

    // Obtener el ID (si existe)
    const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){
        // Agregar el ID del articulo a la lista de eliminados
        articulosEliminados.push(parseInt(inputId.value));
        console.log("Articulo eliminado: "+inputId.value)
    }

    row.remove();
    actualizarTotalVenta()
}

function eliminarEjemplar(boton) {
    const row = boton.closest('tr');
    
    // Obtener el ID (si existe)
    const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){
        // Agregar el ID del ejemplar a la lista de eliminados
        ejemplaresEliminados.push(parseInt(inputId.value));
        console.log("Ejemplar eliminado: "+inputId.value)
    }

    row.remove();
    actualizarTotalVenta()
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//AGREGAR LOS IDs ELIMINADOS COMO INPUTS OCULTOS (Bien)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

document.getElementById("formVenta").addEventListener("submit", function () {
    articulosEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "articulosEliminados";
        input.value = id;
        this.appendChild(input);
    });

    ejemplaresEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "ejemplaresEliminados";
        input.value = id;
        this.appendChild(input);
    });
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ACTUALIZAR SUBTOTAL Y VENTA TOTAL
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function actualizarSubtotal(element) {
    const row = element.closest('tr');
    const cantidad = parseInt(row.querySelector('input[type=number]').value) || 0;
    const precio = parseFloat(row.querySelector('select').selectedOptions[0]?.dataset.precio || 0);
    row.querySelector('input[readonly]').value = (cantidad * precio).toFixed(2);

    actualizarTotalVenta();
}

function actualizarTotalVenta() {
    let total = 0;

    // Subtotales de artículos
    document.querySelectorAll('#articulosContainer input[readonly]').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    // Precios de ejemplares
    document.querySelectorAll('#ejemplaresContainer input[name$=".precio"]').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    document.querySelector('input[name="totalVenta"]').value = total.toFixed(2);
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CODIGO CHATGPT
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contadorBloques = 0;

function agregarBloqueNacimiento() {
    const bloqueId = contadorBloques++;
    const contenedor = document.getElementById('bloquesNacimientos');

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.bloqueId = bloqueId;

    const selectHtml = listaNacimientos.map(n => `
        <option value="${n.id}">
            ${n.monta.macho.nombre} - ${n.monta.hembra.nombre} (${n.fechaNacimiento})
        </option>
        `).join('');

    bloque.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-2">
        <div class="flex-grow-1 me-3">

            <select class="form-select nacimiento-select" onchange="mostrarEjemplares(${bloqueId}, this.value)">
                <option value="">Selecciona un nacimiento</option>
                ${selectHtml}
            </select>

            </div>
                <button type="button" class="btn btn-danger mt-4" onclick="eliminarBloque(${bloqueId})">Eliminar</button>
            </div>

            <div class="row ejemplares-contenedor" id="ejemplares-${bloqueId}">
                <!-- Aqui van los ejemplares dinámicamente -->
            </div>
    `;

    contenedor.appendChild(bloque);
}

function mostrarEjemplares(bloqueId, nacimientoId) {
    const contenedor = document.getElementById(`ejemplares-${bloqueId}`);
    contenedor.innerHTML = '';

    const nacimiento = listaNacimientos.find(n => n.id == nacimientoId);
    if (!nacimiento) return;

    nacimiento.ejemplares.forEach(ejemplar => {
    const col = document.createElement('div');
    col.classList.add('col-md-6');

    col.innerHTML = `
        <div class="ejemplar-card d-flex align-items-center">

            <input type="checkbox" class="form-check-input me-2" name="bloque-${bloqueId}-ejemplar" value="${ejemplar.id}">

            <a href="/img/ejemplares/${ejemplar.nombreImagen}"  target="_blank">
                <img src="/img/ejemplares/${ejemplar.nombreImagen}"  
                    class="img-thumbnail vista-previa" 
                    style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
            </a>

            <div>
                <p class="mb-1"><strong>Sexo:</strong> ${ejemplar.sexo}</p>

                <label class="form-label mb-0">Precio:</label>
                <input type="number" class="form-control form-control-sm" name="precio-${ejemplar.id}" min="0" required>
            </div>

        </div>
    `;
    contenedor.appendChild(col);
    });
}

function eliminarBloque(id) {
    const bloque = document.querySelector(`[data-bloque-id="${id}"]`);
    if (bloque) bloque.remove();
}

document.getElementById('formVenta').addEventListener('submit', function (e) {
    e.preventDefault();

    const seleccionados = [];
    document.querySelectorAll('[data-bloque-id]').forEach(bloque => {
    const bloqueId = bloque.dataset.bloqueId;
    const nacimientoSelect = bloque.querySelector('.nacimiento-select');
    const nacimientoId = nacimientoSelect.value;

    bloque.querySelectorAll(`input[name="bloque-${bloqueId}-ejemplar"]:checked`).forEach(checkbox => {
        const ejemplarId = checkbox.value;
        const precio = document.querySelector(`input[name="precio-${ejemplarId}"]`)?.value;
        seleccionados.push({ nacimientoId, ejemplarId, precio });
    });
    });

    console.log("Ejemplares seleccionados:", seleccionados);
    alert("Consulta la consola para ver la selección.");
});