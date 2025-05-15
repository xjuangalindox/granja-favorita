///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (ARTICULOS Y EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(ventaArticulos.length > 0){
        ventaArticulos.forEach(art => agregarArticuloExistente(art));
    }
    if(ventaEjemplares.length > 0){
        ventaEjemplares.forEach(eje => agregarEjemplarExistente(eje));
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
            <select name="articulos[${contArticulo}].articulo.id" class="form-select" required>
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulos[${contArticulo}].cantidad" class="form-control" min="1" 
                required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulos[${contArticulo}].subtotal" class="form-control" 
                required readonly>
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
            <input type="number" name="ejemplares[${contEjemplar}].precio" class="form-control" min="0" 
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
        <input type="hidden" name="articulos[${contArticulo}].id" value="${art.id}"/>

        <td>
            <select name="articulos[${contArticulo}].articulo.id" class="form-select" required>
                ${opciones}
            </select>
        </td>
        <td>
            <input type="number" name="articulos[${contArticulo}].cantidad" class="form-control" value="${art.cantidad}" 
                min="1" required oninput="actualizarSubtotal(this)">
        </td>
        <td>
            <input type="number" name="articulos[${contArticulo}].subtotal" class="form-control" value="${art.subtotal}" 
                readonly required >
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
//REGISTRAR ARTICULOS Y EJEMPLARES ELIMINADOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let articulosEliminados = [];
let ejemplaresEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ELIMINAR ARTICULO Y EJEMPLAR
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
//AGREGAR LOS IDs ELIMINADOS COMO INPUTS OCULTOS
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
