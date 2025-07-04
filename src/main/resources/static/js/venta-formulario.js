///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (ARTICULOS Y EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(articulosVenta != null){
        console.log("articulos venta existentes: ", articulosVenta);
        if(articulosVenta.length > 0){
            articulosVenta.forEach(art => agregarArticuloExistente(art));
        }
    }

    if(ejemplaresVenta != null){
        console.log("ejemplares venta existentes: ", ejemplaresVenta);
        if(ejemplaresVenta.length > 0){
            agregarNacimientosUtilizados();
        }
    }
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CONTADORES Y LISTAS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contArticulo = 0; // contador para cada ArticuloVenta
let contNacimiento = 0; // solo para que cada nacimiento sea único al eliminarlo
let contEjemplar = 0; // contador para cada EjemplarVenta

const articulosSeleccionados = [];
const nacimientosSeleccionados = [];

let articulosEliminados = [];
let nacimientosEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE ARTICULOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarArticulo(articuloId) {
    articuloId = parseInt(articuloId);
    if (!articulosSeleccionados.includes(articuloId)) {
        articulosSeleccionados.push(articuloId);
        console.log("Artículo agregado:", articuloId);
    }
}

function liberarArticulo(articuloId) {
    const index = articulosSeleccionados.indexOf(articuloId);
    if (index !== -1) { // ¿Encontrado?
        articulosSeleccionados.splice(index, 1);
        console.log("Artículo eliminado:", articuloId);
    }
}

function eliminarArticulo(boton) {
    const row = boton.closest('tr'); // Encontramos la fila del artículo

    const select = row.querySelector('select');
    const articuloId = parseInt(select.value);
    if(articuloId){
        liberarArticulo(articuloId);
    }

    // Buscar dentro del row un input oculto cuyo name termina en .id
    const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){ // Si encontró el input y tiene valor
        articulosEliminados.push(parseInt(inputId.value));
        console.log("ArticuloVenta eliminado: "+inputId.value)
    }

    row.remove();
    actualizarTotalVenta()
}

//*************************************************************************************************************************

function agregarArticulo() {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(art => {
        if (!articulosSeleccionados.includes(art.id)) {
            opciones += `<option value="${art.id}" data-precio="${art.precio}">
                            ${art.presentacion} ${art.nombre}  ($${art.precio})
                        </option>`;
        }
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required
                onchange="guardarArticulo(this.value)">
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

//*************************************************************************************************************************

function agregarArticuloExistente(art) {
    const tbody = document.getElementById('articulosContainer');

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un artículo</option>';

    listaArticulos.forEach(item => {
        if (!articulosSeleccionados.includes(item.id)) {
            const selected = item.id === art.articulo.id ? 'selected' : '';
            opciones += `<option value="${item.id}" data-precio="${item.precio}" ${selected}>
                            ${item.presentacion} ${item.nombre}  ($${item.precio})
                        </option>`;
        }
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <!-- Campo oculto para el id del articulo -->
        <input type="hidden" name="articulosVenta[${contArticulo}].id" value="${art.id}"/>

        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required
                onchange="guardarArticulo(this.value)">
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

    // guardar manualmente los ids de articulos selecionados
    const select = row.querySelector('select');
    if (select.value) {
        guardarArticulo(select.value);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE EJEMPLARES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarNacimiento(nacimientoId) {
    nacimientoId = parseInt(nacimientoId);
    if(!nacimientosSeleccionados.includes(nacimientoId)){
        nacimientosSeleccionados.push(nacimientoId);
        console.log("Nacimiento agregado: ", nacimientoId);
    }
}

function liberarNacimiento(nacimientoId) {
    const index = nacimientosSeleccionados.indexOf(nacimientoId);
    if (index !== -1) { // ¿Encontrado?
        nacimientosSeleccionados.splice(index, 1);
        console.log("Nacimiento eliminado:", nacimientoId);
    }
}

function eliminarNacimiento(boton){
    const bloque = boton.closest('div.mb-4');

    const select = bloque.querySelector('select');
    const nacimientoId = parseInt(select.value);
    if(nacimientoId){
        liberarNacimiento(nacimientoId);
    }
    
    bloque.remove();
    actualizarTotalVenta();
}

function agregarNacimientoEliminado(boton){
    const bloque = boton.closest('div.mb-4');

    const select = bloque.querySelector('select');
    const nacimientoId = parseInt(select.value);
    
    if(nacimientoId){
        nacimientosEliminados.push(nacimientoId);
        console.log("Nacimiento existente eliminado: ",nacimientoId);
    }
}

function onCheckboxChange(checkbox) {
    const precioInput = checkbox.closest('.p-2').querySelector('.precio-input');
    if (checkbox.checked) { // Precio disponible y obligatorio
        precioInput.removeAttribute('readonly');
        precioInput.setAttribute('required', 'required');
    
    } else { // Precio bloquado y opcional
        precioInput.setAttribute('readonly', 'readonly');
        precioInput.removeAttribute('required');

        precioInput.value = ''; // Opcional: limpia el valor si se desmarca
        actualizarTotalVenta(); // Actualizar el total de la venta
    }
}

//*************************************************************************************************************************

// 1. Función global para formatear opciones
function formatOption(state) {
    if (!state.id) return state.text;

    const imgMacho = $(state.element).data('img-macho');
    const imgHembra = $(state.element).data('img-hembra');
    const name = state.text;

    return $(`
        <div class="option-wrapper">
            <img src="${imgMacho}" class="img-select" title="Macho" />
            <img src="${imgHembra}" class="img-select" title="Hembra" />
            <span>&nbsp;&nbsp;${name}</span>
        </div>
    `);
}

$(document).ready(function() {
    // Inicializas Select2 para selects que ya están en el HTML al cargar la página
    $('#nacimiento-0').select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });
});

function formatearFecha(fechaISO){
    if(!fechaISO) return '';
    return new Intl.DateTimeFormat('es-MX', {
        day: '2-digit',
        month: 'long',
        year: 'numeric'
    }).format(new Date(fechaISO));
}

function agregarNacimiento() {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(nac => {
        if (!nacimientosSeleccionados.includes(nac.id)) {

            const fechaFormateada = formatearFecha(nac.fechaNacimiento);
            opciones += `<option 
                            value="${nac.id}"
                            data-img-macho="/img/conejos/${nac.monta.macho.nombreImagen}"
                            data-img-hembra="/img/conejos/${nac.monta.hembra.nombreImagen}">

                            ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${fechaFormateada})
                        </option>`;
        }
    });

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">
            <select id="nacimiento-${nacimientoIndex}" class="form-select" required
                onchange="guardarNacimiento(this.value); 
                mostrarEjemplares(this.value, ${nacimientoIndex})">
                ${opciones}
            </select>

            <button type="button" class="btn btn-danger btn-sm mx-2" onclick="eliminarNacimiento(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </div>

        <div id="ejemplares-${nacimientoIndex}">
            <!-- Aqui van los ejemplares dinámicamente -->
        </div>
    `;

    contenedor.appendChild(bloque);

    // 3. Inicializas Select2 solo en el nuevo select creado
    $(`#nacimiento-${nacimientoIndex}`).select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });
}

function mostrarEjemplares(nacimientoId, nacimientoIndex) {
    const contenedor = document.getElementById(`ejemplares-${nacimientoIndex}`);
    contenedor.innerHTML = ''; // Limpiar contenido anterior

    const row = document.createElement('div');
    row.classList.add('row', 'g-3'); // g-3 para espacio entre columnas

    const nacimientoDTO = listaNacimientos.find(nac => nac.id == nacimientoId);
    if(!nacimientoDTO) return;

    nacimientoDTO.ejemplares.forEach(ejemplar => {
        const col = document.createElement('div');
        col.classList.add('col-md-6', 'col-lg-4'); // 2 o 3 columnas dependiendo del tamaño de pantalla

        col.innerHTML = `
            <div class="p-2 border rounded">
                <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">

                <input class="form-check-input mb-2" 
                    type="checkbox" 
                    name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
                    onchange="onCheckboxChange(this)">

                <a href="/img/ejemplares/${ejemplar.nombreImagen || 'default.jpg'}"  target="_blank">
                    <img src="/img/ejemplares/${ejemplar.nombreImagen || 'default.jpg'}"  
                        class="img-thumbnail vista-previa" 
                        style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
                </a>

                <div>
                    <p class="mb-1"><strong>Sexo:</strong> ${ejemplar.sexo}</p>

                    <label class="form-label mb-0">Precio:</label>
                    <input class="form-control precio-input" 
                        type="number" 
                        name="ejemplaresVenta[${contEjemplar}].precio"
                        min="0"
                        readonly
                        oninput="actualizarTotalVenta()">
                </div>
            </div>
        `;
        row.appendChild(col);
        contEjemplar++;
    });

    contenedor.appendChild(row); // Agrega la fila completa
}

//*************************************************************************************************************************

function agregarNacimientosUtilizados() {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Generar valor por defecto del select
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(nac => {
        let selected = '';

        // Evitar nacimientos repetidos select
        if (!nacimientosSeleccionados.includes(nac.id)) {
        
            // Seleccionar nacimientos utilizados
            if(idsNacimientosUtilizados.includes(nac.id)){
                selected = 'selected';
            }
            
            const fechaFormateada = formatearFecha(nac.fechaNacimiento);
            opciones += `<option 
                            value="${nac.id}"
                            ${selected}
                            data-img-macho="/img/conejos/${nac.monta.macho.nombreImagen}"
                            data-img-hembra="/img/conejos/${nac.monta.hembra.nombreImagen}">

                            ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${fechaFormateada})
                        </option>`;
        }
    });

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">

            <select id="nacimiento-${nacimientoIndex}" class="form-select" required 
                onchange="guardarNacimiento(this.value);
                mostrarEjemplaresExistentes(this.value, ${nacimientoIndex})">
                
                ${opciones}
            </select>

            <button type="button" class="btn btn-danger btn-sm mx-2" 
                onclick="eliminarNacimiento(this); 
                agregarNacimientoEliminado(this)">
                
                <i class="bi bi-x-lg"></i>
            </button>

        </div>

        <div id="ejemplares-${nacimientoIndex}">
            <!-- Aqui van los ejemplares dinámicamente -->
        </div>
    `;

    contenedor.appendChild(bloque);

    // Inicializar Select2 en el nuevo select creado
    $(`#nacimiento-${nacimientoIndex}`).select2({
        templateResult: formatOption,
        templateSelection: formatOption,
        minimumResultsForSearch: -1
    });

    // Invocar el método manualmente porque no se ejecuta por progrmación
    const select = bloque.querySelector('select');
    if (select.value) {
        mostrarEjemplaresExistentes(select.value, nacimientoIndex);

        // Agregar el ID del nacimiento a nacimientosSeleccionados si no está
        const nacimientoId = parseInt(select.value);
        if (!nacimientosSeleccionados.includes(nacimientoId)) {
            nacimientosSeleccionados.push(nacimientoId);
            console.log("Nacimiento agregado manualmente al final: ", nacimientoId);
        }
    }
}

function mostrarEjemplaresExistentes(nacimientoId, nacimientoIndex) {
    console.log("id nacimiento con ejemplares vendidos: ", nacimientoId);

    const contenedor = document.getElementById(`ejemplares-${nacimientoIndex}`);
    contenedor.innerHTML = ''; // Limpiar contenido anterior

    const row = document.createElement('div');
    row.classList.add('row', 'g-3'); // g-3 para espacio entre columnas

    // Obtener nacimiento
    const nacimiento = listaNacimientos.find(nac => nac.id == nacimientoId);
    if(!nacimiento) return;

    // Mostrar o crear ejemplares venta del nacimiento
    nacimiento.ejemplares.forEach(ejemplar => {
        const col = document.createElement('div');
        col.classList.add('col-md-6', 'col-lg-4'); // 2 o 3 columnas dependiendo del tamaño de pantalla

        // Comprobar disponibilidad del ejemplar
        const ejemplarVenta = ejemplaresVenta.find(ejeV => ejeV.ejemplar.id == ejemplar.id);
        
        // Mostrar ejemplar vendido
        if(ejemplarVenta){
            col.innerHTML = `
                <div class="p-2 border rounded">
                    <input type="hidden" name="ejemplaresVenta[${contEjemplar}].id" value="${ejemplarVenta.id}">
                    <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplarVenta.ejemplar.id}">

                    <input class="form-check-input mb-2" 
                        type="checkbox" 
                        name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
                        ${ejemplarVenta.ejemplar.vendido ? 'checked' : ''}
                        onchange="onCheckboxChange(this)">

                    <a href="/img/ejemplares/${ejemplarVenta.ejemplar.nombreImagen || 'default.jpg'}"  target="_blank">
                        <img src="/img/ejemplares/${ejemplarVenta.ejemplar.nombreImagen || 'default.jpg'}"  
                            class="img-thumbnail vista-previa" 
                            style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
                    </a>

                    <div>
                        <p class="mb-1"><strong>Sexo:</strong> ${ejemplarVenta.ejemplar.sexo}</p>

                        <label class="form-label mb-0">Precio:</label>
                        <input class="form-control precio-input" 
                            type="number" 
                            name="ejemplaresVenta[${contEjemplar}].precio" 
                            value="${ejemplarVenta.precio ?? ''}"
                            min="0"
                            ${!ejemplarVenta.ejemplar.vendido ? 'readonly' : ''}
                            ${ejemplarVenta.ejemplar.vendido ? 'required' : ''}
                            oninput="actualizarTotalVenta()">
                    </div>
                </div>
            `;

        // Mostrar ejemplar disponible
        }else{
            col.innerHTML = `
                <div class="p-2 border rounded">
                    <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">

                    <input class="form-check-input mb-2" 
                        type="checkbox" 
                        name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
                        value="true"
                        onchange="onCheckboxChange(this)"
                        ${ejemplar.vendido ? 'checked' : ''}>

                    <a href="/img/ejemplares/${ejemplar.nombreImagen || 'default.jpg'}"  target="_blank">
                        <img src="/img/ejemplares/${ejemplar.nombreImagen || 'default.jpg'}"  
                            class="img-thumbnail vista-previa" 
                            style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
                    </a>

                    <div>
                        <p class="mb-1"><strong>Sexo:</strong> ${ejemplar.sexo}</p>

                        <label class="form-label mb-0">Precio:</label>
                        <input class="form-control precio-input" 
                            type="number" 
                            name="ejemplaresVenta[${contEjemplar}].precio"
                            min="0"
                            ${!ejemplar.vendido ? 'readonly' : ''}
                            ${ejemplar.vendido ? 'required' : ''}
                            oninput="actualizarTotalVenta()">
                    </div>
                </div>
            `;
        }

        row.appendChild(col);
        contEjemplar++;
    });

    contenedor.appendChild(row); // Agrega la fila completa
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

    nacimientosEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "nacimientosEliminados";
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
    document.querySelectorAll('#nacimientosContainer input[name$=".precio"]').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    document.querySelector('input[name="totalVenta"]').value = total.toFixed(2);
}
