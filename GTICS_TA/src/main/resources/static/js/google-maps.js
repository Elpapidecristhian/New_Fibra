/**
 * Google Maps Integration para GTICS San Miguel
 * Funcionalidades:
 * - Búsqueda de direcciones
 * - Marcador arrastrable
 * - Restricción a distrito de San Miguel, Lima
 * - Autocompletado de direcciones
 */

let map;
let marker;
let geocoder;
let autocomplete;

// Configuración para San Miguel, Lima
const SAN_MIGUEL_BOUNDS = {
    north: -12.0600,
    south: -12.1000,
    east: -77.0800,
    west: -77.1200
};

const SAN_MIGUEL_CENTER = {
    lat: -12.0800,
    lng: -77.1000
};

/**
 * Inicializa el mapa de Google Maps
 * @param {string} mapElementId - ID del elemento HTML donde se mostrará el mapa
 * @param {string} addressInputId - ID del input de dirección
 * @param {Object} options - Opciones adicionales
 */
function initializeGoogleMap(mapElementId, addressInputId, options = {}) {
    const mapElement = document.getElementById(mapElementId);
    const addressInput = document.getElementById(addressInputId);
    
    if (!mapElement || !addressInput) {
        console.error('Elementos del mapa no encontrados');
        return;
    }

    // Configuración del mapa
    const mapOptions = {
        zoom: 15,
        center: SAN_MIGUEL_CENTER,
        restriction: {
            latLngBounds: SAN_MIGUEL_BOUNDS,
            strictBounds: false
        },
        mapTypeControl: true,
        streetViewControl: true,
        fullscreenControl: true,
        ...options
    };

    // Crear mapa
    map = new google.maps.Map(mapElement, mapOptions);
    geocoder = new google.maps.Geocoder();

    // Crear marcador arrastrable
    marker = new google.maps.Marker({
        position: SAN_MIGUEL_CENTER,
        map: map,
        draggable: true,
        title: 'Ubicación seleccionada'
    });

    // Configurar autocompletado
    setupAutocomplete(addressInput);

    // Event listeners
    setupEventListeners(addressInput);
}

/**
 * Configura el autocompletado de Google Places
 */
function setupAutocomplete(addressInput) {
    autocomplete = new google.maps.places.Autocomplete(addressInput, {
        bounds: new google.maps.LatLngBounds(
            new google.maps.LatLng(SAN_MIGUEL_BOUNDS.south, SAN_MIGUEL_BOUNDS.west),
            new google.maps.LatLng(SAN_MIGUEL_BOUNDS.north, SAN_MIGUEL_BOUNDS.east)
        ),
        strictBounds: true,
        componentRestrictions: { country: 'pe' },
        fields: ['address_components', 'geometry', 'name', 'formatted_address']
    });

    autocomplete.addListener('place_changed', function() {
        const place = autocomplete.getPlace();
        if (place.geometry) {
            updateMapLocation(place.geometry.location, place.formatted_address);
        }
    });
}

/**
 * Configura los event listeners del mapa
 */
function setupEventListeners(addressInput) {
    // Cuando se arrastra el marcador
    marker.addListener('dragend', function() {
        const position = marker.getPosition();
        reverseGeocode(position, addressInput);
    });

    // Click en el mapa
    map.addListener('click', function(event) {
        updateMapLocation(event.latLng);
        reverseGeocode(event.latLng, addressInput);
    });
}

/**
 * Actualiza la ubicación del mapa y marcador
 */
function updateMapLocation(location, address = null) {
    map.setCenter(location);
    marker.setPosition(location);
    
    if (address) {
        marker.setTitle(address);
    }
}

/**
 * Geocodificación inversa - obtiene dirección desde coordenadas
 */
function reverseGeocode(location, addressInput) {
    geocoder.geocode({ location: location }, function(results, status) {
        if (status === 'OK' && results[0]) {
            const address = results[0].formatted_address;
            addressInput.value = address;
            marker.setTitle(address);
            
            // Trigger change event para formularios
            addressInput.dispatchEvent(new Event('change'));
        }
    });
}

/**
 * Busca una dirección y actualiza el mapa
 */
function searchAddress(address, addressInput) {
    if (!address.trim()) return;

    geocoder.geocode({
        address: address,
        bounds: new google.maps.LatLngBounds(
            new google.maps.LatLng(SAN_MIGUEL_BOUNDS.south, SAN_MIGUEL_BOUNDS.west),
            new google.maps.LatLng(SAN_MIGUEL_BOUNDS.north, SAN_MIGUEL_BOUNDS.east)
        ),
        componentRestrictions: { country: 'pe' }
    }, function(results, status) {
        if (status === 'OK' && results[0]) {
            const location = results[0].geometry.location;
            updateMapLocation(location, results[0].formatted_address);
            addressInput.value = results[0].formatted_address;
        } else {
            alert('No se pudo encontrar la dirección en San Miguel. Por favor, intente con otra dirección.');
        }
    });
}

/**
 * Muestra/oculta el mapa
 */
function toggleMap(mapElementId, buttonElement) {
    const mapElement = document.getElementById(mapElementId);
    if (mapElement.style.display === 'none' || !mapElement.style.display) {
        mapElement.style.display = 'block';
        buttonElement.textContent = 'Ocultar Mapa';
        // Redimensionar mapa después de mostrarlo
        setTimeout(() => {
            google.maps.event.trigger(map, 'resize');
            map.setCenter(marker.getPosition());
        }, 100);
    } else {
        mapElement.style.display = 'none';
        buttonElement.textContent = 'Mostrar en Mapa';
    }
}

/**
 * Obtiene las coordenadas actuales del marcador
 */
function getCurrentLocation() {
    if (marker) {
        const position = marker.getPosition();
        return {
            lat: position.lat(),
            lng: position.lng()
        };
    }
    return null;
}

/**
 * Función de utilidad para inicializar mapas en formularios
 */
function initFormMap(formId) {
    const mapId = formId + '-map';
    const addressId = formId + '-address';
    const toggleButtonId = formId + '-toggle-map';
    
    // Crear elementos si no existen
    createMapElements(formId, mapId, toggleButtonId);
    
    // Inicializar mapa
    initializeGoogleMap(mapId, addressId);
    
    // Configurar botón toggle
    const toggleButton = document.getElementById(toggleButtonId);
    if (toggleButton) {
        toggleButton.addEventListener('click', function() {
            toggleMap(mapId, this);
        });
    }
}

/**
 * Crea elementos HTML necesarios para el mapa
 */
function createMapElements(formId, mapId, toggleButtonId) {
    const addressInput = document.querySelector(`#${formId} input[name*="direccion"], #${formId} input[name*="address"]`);
    if (!addressInput) return;
    
    addressInput.id = formId + '-address';
    
    // Crear botón toggle si no existe
    if (!document.getElementById(toggleButtonId)) {
        const toggleButton = document.createElement('button');
        toggleButton.id = toggleButtonId;
        toggleButton.type = 'button';
        toggleButton.className = 'btn btn-info btn-sm mt-2';
        toggleButton.textContent = 'Mostrar en Mapa';
        addressInput.parentNode.appendChild(toggleButton);
    }
    
    // Crear contenedor del mapa si no existe
    if (!document.getElementById(mapId)) {
        const mapContainer = document.createElement('div');
        mapContainer.id = mapId;
        mapContainer.style.height = '300px';
        mapContainer.style.width = '100%';
        mapContainer.style.display = 'none';
        mapContainer.style.marginTop = '10px';
        mapContainer.style.border = '1px solid #ddd';
        mapContainer.style.borderRadius = '4px';
        addressInput.parentNode.appendChild(mapContainer);
    }
}

// Función global para callback de Google Maps API
window.initMap = function() {
    console.log('Google Maps API cargada correctamente');
};
