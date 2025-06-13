///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (ARTICULOS Y EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(articulosVenta.length > 0){
        articulosVenta.forEach(art => agregarArticuloExistente(art));
    }
    if(idNacimientos.length > 0){
        agregarNacimientosExistentes();
    }
    //if(ejemplaresVenta.length > 0){
    //    ejemplaresVenta.forEach(eje => agregarNacimientosExistentes(eje));
    //}
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CONTADORES Y LISTAS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contArticulo = 0; // contador para cada ArticuloVenta
let contNacimiento = 0; // solo para que cada nacimiento sea único al eliminarlo
let contEjemplar = 0; // contador para cada EjemplarVenta

const listaIdsArticulosSeleccionados = [];
const listaIdsNacimientosSeleccionados = [];

let articulosEliminados = [];
let ejemplaresEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE ARTICULOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarIdArticuloSeleccionado(articuloId) {
    articuloId = parseInt(articuloId);
    if (!listaIdsArticulosSeleccionados.includes(articuloId)) {
        listaIdsArticulosSeleccionados.push(articuloId);
        console.log("Artículos seleccionados:", listaIdsArticulosSeleccionados);
    }
}

function liberarArticulo(articuloId) {
    const index = listaIdsArticulosSeleccionados.indexOf(articuloId);
    if (index !== -1) { // ¿Encontrado?
        listaIdsArticulosSeleccionados.splice(index, 1);
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

    listaArticulos.forEach(item => {
        if (!listaIdsArticulosSeleccionados.includes(item.id)) {
            opciones += `<option value="${item.id}" data-precio="${item.precio}">
                            ${item.presentacion} ${item.nombre}  ($${item.precio})
                        </option>`;
        }
    });

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <select name="articulosVenta[${contArticulo}].articulo.id" class="form-select" required
                onchange="guardarIdArticuloSeleccionado(this.value)">
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
        if (!listaIdsArticulosSeleccionados.includes(item.id)) {
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
                onchange="guardarIdArticuloSeleccionado(this.value)">
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

    // Obtener el valor del select y guardarlo para no repetirlo
    const select = row.querySelector('select');
    if (select.value) {
        guardarIdArticuloSeleccionado(select.value);
    }
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREACION Y MODIFICACION DE EJEMPLARES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function guardarIdNacimientoSeleccionado(nacimientoId) {
    nacimientoId = parseInt(nacimientoId);
    if (!listaIdsNacimientosSeleccionados.includes(nacimientoId)) {
        listaIdsNacimientosSeleccionados.push(nacimientoId);
        console.log("Nacimientos seleccionados:", listaIdsNacimientosSeleccionados);
    }
}

function liberarNacimiento(nacimientoId) {
    const index = listaIdsNacimientosSeleccionados.indexOf(nacimientoId);
    if (index !== -1) { // ¿Encontrado?
        listaIdsNacimientosSeleccionados.splice(index, 1);
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

    // Buscar dentro del row un input oculto cuyo name termina en .id
    /*const inputId = row.querySelector('input[type="hidden"][name$=".id"]');
    if(inputId && inputId.value){ // Si encontró el input y tiene valor
        articulosEliminados.push(parseInt(inputId.value));
        console.log("ArticuloVenta eliminado: "+inputId.value)
    }*/

    bloque.remove();
    actualizarTotalVenta();
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

function agregarNacimiento() {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(nac => {
        if (!listaIdsNacimientosSeleccionados.includes(nac.id)) {
            opciones += `<option value="${nac.id}">
                            ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${nac.fechaNacimiento})
                        </option>`;
        }
    });

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">
            <select class="form-select" required
                onchange="guardarIdNacimientoSeleccionado(this.value); mostrarEjemplares(this.value, ${nacimientoIndex})">
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
                    value="true"
                    onchange="onCheckboxChange(this)"
                    ${ejemplar.vendido ? 'checked' : ''}>

                <a href="/img/ejemplares/${ejemplar.nombreImagen}"  target="_blank">
                    <img src="/img/ejemplares/${ejemplar.nombreImagen}"  
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
        row.appendChild(col);
        contEjemplar++;
    });

    contenedor.appendChild(row); // Agrega la fila completa
}

//*************************************************************************************************************************

function agregarNacimientosExistentes() {
    const contenedor = document.getElementById('nacimientosContainer');
    const nacimientoIndex = contNacimiento++;

    // Generamos las opciones del select en JS
    let opciones = '<option value="">Selecciona un nacimiento</option>';

    listaNacimientos.forEach(nac => {
        let selected = '';
        if (!listaIdsNacimientosSeleccionados.includes(nac.id)) {
            if(idNacimientos.includes(nac.id)){
                selected = 'selected';
            }
            
            opciones += `<option value="${nac.id}" ${selected}>
                            ${nac.monta.macho.nombre} - ${nac.monta.hembra.nombre} (${nac.fechaNacimiento})
                        </option>`;
        }
    });

    const bloque = document.createElement('div');
    bloque.classList.add('mb-4', 'border', 'p-3', 'rounded');
    bloque.dataset.nacimientoIndex = nacimientoIndex;

    bloque.innerHTML = `
        <div class="d-flex justify-content-between align-items-start mb-2">
            <select class="form-select" required 
                onchange="guardarIdNacimientoSeleccionado(this.value); mostrarEjemplaresExistentes(this.value, ${nacimientoIndex})">
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

    // Obtener el valor del select, guardarlo para no repetirlo
    const select = bloque.querySelector('select');
    if (select.value) {
        guardarIdNacimientoSeleccionado(select.value);
        mostrarEjemplaresExistentes(select.value, nacimientoIndex);
    }
}

function mostrarEjemplaresExistentes(nacimientoId, nacimientoIndex) {
    console.log("nacimientoId: ", nacimientoId);

    const contenedor = document.getElementById(`ejemplares-${nacimientoIndex}`);
    contenedor.innerHTML = ''; // Limpiar contenido anterior

    const row = document.createElement('div');
    row.classList.add('row', 'g-3'); // g-3 para espacio entre columnas

    const nacimientoDTO = listaNacimientos.find(nac => nac.id == nacimientoId);
    if(!nacimientoDTO) return;

    nacimientoDTO.ejemplares.forEach(ejemplar => {
        let encontrado = false;
 
        const col = document.createElement('div');
        col.classList.add('col-md-6', 'col-lg-4'); // 2 o 3 columnas dependiendo del tamaño de pantalla

        //Crear EjemplarVenta (nuevo o existente)
        ejemplaresVenta.forEach(ejemplarVenta => {
            // Crear ejemplar existente
            if(ejemplarVenta.ejemplar.id == ejemplar.id){
                col.innerHTML = `
                    <div class="p-2 border rounded">
                        <input type="hidden" name="ejemplaresVenta[${contEjemplar}].id" value="${ejemplarVenta.id}">
                        <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplarVenta.ejemplar.id}">

                        <input class="form-check-input mb-2" 
                            type="checkbox" 
                            name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
                            ${ejemplarVenta.ejemplar.vendido ? 'checked' : ''}
                            onchange="onCheckboxChange(this)">

                        <a href="/img/ejemplares/${ejemplarVenta.ejemplar.nombreImagen}"  target="_blank">
                            <img src="/img/ejemplares/${ejemplarVenta.ejemplar.nombreImagen}"  
                                class="img-thumbnail vista-previa" 
                                style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
                        </a>

                        <div>
                            <p class="mb-1"><strong>Sexo:</strong> ${ejemplarVenta.ejemplar.sexo}</p>

                            <label class="form-label mb-0">Precio:</label>
                            <input class="form-control precio-input" 
                                type="number" 
                                name="ejemplaresVenta[${contEjemplar}].precio" 
                                value="${ejemplarVenta.precio}"
                                min="0"
                                ${!ejemplarVenta.ejemplar.vendido ? 'readonly' : ''}
                                ${ejemplarVenta.ejemplar.vendido ? 'required' : ''}
                                oninput="actualizarTotalVenta()">
                        </div>
                    </div>
                `;
                
                encontrado = true;
            }
        });

        // Crear ejemplar nuevo
        if(!encontrado){
            col.innerHTML = `
                <div class="p-2 border rounded">
                    <input type="hidden" name="ejemplaresVenta[${contEjemplar}].ejemplar.id" value="${ejemplar.id}">

                    <input class="form-check-input mb-2" 
                        type="checkbox" 
                        name="ejemplaresVenta[${contEjemplar}].ejemplar.vendido" 
                        value="true"
                        onchange="onCheckboxChange(this)"
                        ${ejemplar.vendido ? 'checked' : ''}>

                    <a href="/img/ejemplares/${ejemplar.nombreImagen}"  target="_blank">
                        <img src="/img/ejemplares/${ejemplar.nombreImagen}"  
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
    document.querySelectorAll('#nacimientosContainer input[name$=".precio"]').forEach(input => {
        total += parseFloat(input.value) || 0;
    });

    document.querySelector('input[name="totalVenta"]').value = total.toFixed(2);
}



































///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//SIN USO
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

/*function eliminarEjemplar(boton) {
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
}*/
