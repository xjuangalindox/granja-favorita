///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//VALIDAR UPDATE (EJEMPLARES)
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener('DOMContentLoaded', () => {    
    if(ejemplares.length > 0){
        console.log("Ejemplares existentes: " + ejemplares.length);
        ejemplares.forEach(eje => agregarEjemplarExistente(eje));
    }
});

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CONTADOR DE LISTA EJEMPLARES
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let contador = 0;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//CREAR - EJEMPLAR
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarEjemplar() {
    const tbody = document.getElementById('ejemplaresContainer');

    const row = document.createElement('tr');
    row.innerHTML = `
        <td>
            <div class="d-flex flex-column align-items-center">
                <input class="form-control" type="file" name="ejemplares[${contador}].imagen" accept="image/*" required 
                    onchange="mostrarVistaPrevia(this)">

                <img class="img-thumbnail vista-previa" 
                    style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
            </div>
        </td>
        <td>
            <select class="form-select" name="ejemplares[${contador}].sexo" required>
                <option value="">Selecciona una opción</option>
                <option value="Macho">Macho</option>
                <option value="Hembra">Hembra</option>
            </select>
        </td>
        <td>
            <input class="form-check-input" type="checkbox" name="ejemplares[${contador}].vendido">
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contador++;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//UPDATE - EJEMPLAR EXISTENTE
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function agregarEjemplarExistente(eje) {
    const tbody = document.getElementById('ejemplaresContainer');

    const row = document.createElement('tr');
    row.innerHTML = `
        <!-- Campo oculto para el id del ejemplar -->
        <input type="hidden" name="ejemplares[${contador}].id" value="${eje.id}"/>
        <input type="hidden" name="ejemplares[${contador}].nombreImagen" value="${eje.nombreImagen}"/>

        <td>
            <div class="d-flex flex-column align-items-center">
                <input type="file" name="ejemplares[${contador}].imagen" class="form-control" accept="image/*" 
                    onchange="mostrarVistaPrevia(this)">

                <img class="img-thumbnail vista-previa" 
                    src="/img/ejemplares/${eje.nombreImagen}" 
                    style="max-width: 100px; max-height: 100px; object-fit: cover;"/>
            </div>
        </td>
        <td>
            <select class="form-select" name="ejemplares[${contador}].sexo" required>
                <option value="">Selecciona una opción</option>
                <option value="Macho" ${eje.sexo === 'Macho' ? 'selected' : ''}>Macho</option>
                <option value="Hembra" ${eje.sexo === 'Hembra' ? 'selected' : ''}>Hembra</option>
            </select>
        </td>
        <td>
            <input class="form-check-input" type="checkbox" name="ejemplares[${contador}].vendido" ${eje.vendido ? 'checked' : ''}>
        </td>

        <td>
            <button type="button" class="btn btn-danger btn-sm" onclick="eliminarEjemplar(this)">
                <i class="bi bi-x-lg"></i>
            </button>
        </td>
    `;
    
    tbody.appendChild(row);
    contador++;
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
//REGISTRAR EJEMPLARES ELIMINADOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

let ejemplaresEliminados = [];

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//ELIMINAR ARTICULO Y EJEMPLAR
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    //actualizarTotalVenta()
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//AGREGAR LOS IDs ELIMINADOS COMO INPUTS OCULTOS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

document.getElementById("formNacimiento").addEventListener("submit", function () {
    ejemplaresEliminados.forEach(id => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "ejemplaresEliminados";
        input.value = id;
        this.appendChild(input);
    });
});
