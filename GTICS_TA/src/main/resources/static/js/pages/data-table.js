//[Data Table Javascript]

//Project:	Rhythm Admin - Responsive Admin Template
//Primary use:   Used only for the Data Table

$(function () {
    "use strict";

    $('#example1').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#example2').DataTable({
        paging      : true,
        lengthChange: false,
        searching   : false,
        ordering    : true,
        info        : true,
        autoWidth   : false,
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#example').DataTable({
        dom: 'Bfrtip',
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#tickets').DataTable({
        paging      : true,
        lengthChange: true,
        searching   : true,
        ordering    : true,
        info        : true,
        autoWidth   : false,
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#productorder').DataTable({
        paging      : true,
        lengthChange: true,
        searching   : true,
        ordering    : true,
        info        : true,
        autoWidth   : false,
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#complex_header').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    //--------Individual column searching

    // Setup - add a text input to each footer cell
    $('#example5 tfoot th').each(function () {
        var title = $(this).text();
        $(this).html('<input type="text" placeholder="Buscar ' + title + '" />');
    });

    // DataTable
    var table = $('#example5').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    // Apply the search
    table.columns().every(function () {
        var that = this;
        $('input', this.footer()).on('keyup change', function () {
            if (that.search() !== this.value) {
                that
                    .search(this.value)
                    .draw();
            }
        });
    });

    //---------------Form inputs
    var table6 = $('#example6').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.5/i18n/es-ES.json'
        }
    });

    $('#data-update').click(function () {
        var data = table6.$('input, select').serialize();
        alert(
            "Los siguientes datos habrían sido enviados al servidor: \n\n" +
            data.substr(0, 120) + '...'
        );
        return false;
    });
}); // End of use strict
