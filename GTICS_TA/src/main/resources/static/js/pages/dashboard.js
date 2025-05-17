//[Dashboard Javascript]

//Project:	Rhythm Admin - Responsive Admin Template
//Primary use:   Used only for the main dashboard (index.html)


$(function () {

    'use strict';

    // Primer gráfico
    var options1 = {
        series: [{
            name: 'Reporte económico',
            data: [76, 85, 121]
        }, {
            name: 'Cantidad de Canchas reservadas',
            data: [140, 140, 140]
        }],
        chart: {
            type: 'bar',
            foreColor:"#bac0c7",
            height: 289,
            toolbar: { show: false }
        },
        plotOptions: {
            bar: {
                endingShape: 'rounded',
                horizontal: false,
                columnWidth: '50%',
            },
        },
        dataLabels: { enabled: false },
        grid: { show: false },
        stroke: { show: true, width: 3, colors: ['transparent'] },
        colors: ['#00D0FF', '#3246D3'],
        xaxis: { categories: ['Abril', 'Mayo', 'Julio'] },
        legend: { show: true },
        tooltip: {
            y: { formatter: function (val) { return "$ " + val + " thousands"; }},
            marker: { show: false },
        }
    };

    var chart1 = new ApexCharts(document.querySelector("#recent_trend_1"), options1);
    chart1.render();
    var options2 = {
        series: [{
            name: 'Reporte económico',
            data: [150, 200, 130]  // Datos diferentes para el segundo gráfico
        }, {
            name: 'Cantidad de Canchas reservadas',
            data: [150, 135, 120]  // Otros datos diferentes para el segundo gráfico
        }],
        chart: {
            type: 'bar',
            foreColor:"#bac0c7",
            height: 289,
            toolbar: { show: false }
        },
        plotOptions: {
            bar: {
                endingShape: 'rounded',
                horizontal: false,
                columnWidth: '50%',
            },
        },
        dataLabels: { enabled: false },
        grid: { show: false },
        stroke: { show: true, width: 3, colors: ['transparent'] },
        colors: ['#00D0FF', '#3246D3'],
        xaxis: { categories: ['Enero', 'Febrero', 'Marzo'] }, // Cambié las categorías para este gráfico
        legend: { show: true },
        tooltip: {
            y: { formatter: function (val) { return "$ " + val + " thousands"; }},
            marker: { show: false },
        }
    };


    var chart2 = new ApexCharts(document.querySelector("#recent_trend_2"), options2);
    chart2.render();



    var options = {
        series: [
            {
                name: "Outcome",
                data: [122, 222, 142, 182]
            }
        ],
        chart: {
            height: 100,
            width: 150,
            type: 'area',
            toolbar: {
                show: false
            }
        },
        colors: ['#77B6EA'],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: 'smooth',
            width: 1,
        },
        grid: {
            show: false,
        },
        yaxis: {
            labels: {
                show: false
            },
            axisTicks: {
                show: false
            }
        },
        xaxis: {
            categories: ['Abril', 'Mayo', 'Julio'],
            labels: {
                show: false
            },
            axisTicks: {
                show: false
            }
        },
        legend: {
            show: false,
        }
    };

    var chart = new ApexCharts(document.querySelector("#balance1"), options);
    chart.render();



    var options = {
        series: [
            {
                name: "Outcome",
                data: [102, 124, 92, 85]
            }
        ],
        chart: {
            height: 100,
            width: 150,
            type: 'area',
            toolbar: {
                show: false
            }
        },
        colors: ['#ee3158'],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: 'smooth',
            width: 1,
        },
        grid: {
            show: false,
        },
        yaxis: {
            labels: {
                show: false
            },
            axisTicks: {
                show: false
            }
        },
        xaxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr'],
            labels: {
                show: false
            },
            axisTicks: {
                show: false
            }
        },
        legend: {
            show: false,
        }
    };

    var chart = new ApexCharts(document.querySelector("#balance2"), options);
    chart.render();




    var options = {
        series: [
            {
                name: "Discharge Patient",
                data: [12, 22, 14, 18, 22 , 13, 17]
            },
            {
                name: "Admit Patient",
                data: [28, 39, 23, 36, 45, 32, 43]
            }
        ],
        chart: {
            height: 275,
            type: 'line',
            dropShadow: {
                enabled: true,
                color: '#000',
                top: 18,
                left: 7,
                blur: 10,
                opacity: 0.2
            },
            toolbar: {
                show: false
            }
        },
        colors: ['#ee3158', '#1dbfc1'],
        dataLabels: {
            enabled: false,
        },
        stroke: {
            curve: 'smooth'
        },
        grid: {
            borderColor: '#e7e7e7',
        },
        xaxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
        },
        legend: {
            show: false,
        }
    };

    var chart = new ApexCharts(document.querySelector("#overview_trend"), options);
    chart.render();

    var options = {
        series: [{
            name: 'Servicios más reservados',
            data: [120, 85, 150, 90] // Estos son los valores de reservas
        }],
        chart: {
            type: 'bar',
            height: 350
        },
        plotOptions: {
            bar: {
                horizontal: true, // Hace el gráfico de barras horizontal
                endingShape: 'rounded',
                columnWidth: '50%'
            }
        },
        dataLabels: {
            enabled: false
        },
        xaxis: {
            categories: ['Top1: Piscina Pozo', 'Top2: Piscina Ariana', 'Top3: Cancha Fernandito ','Top4: Pista de Atletismo Yarlequé'],
        },
        colors: ['#00D0FF'],
        tooltip: {
            y: {
                formatter: function(val) {
                    return val + " reservas";
                }
            }
        }
    };

    var chart = new ApexCharts(document.querySelector("#top_services"), options);
    chart.render();




    var options = {
        series: [35, 65, 40, 50 ,60],
        chart: {
            height: 345,
            type: 'polarArea'
        },
        labels: ['Gimnasio', 'Cancha de Césped', 'Cancha de loza', 'Pista de Atleismo', 'Piscina'],
        fill: {
            opacity: 1
        },
        stroke: {
            width: 1,
            colors: undefined
        },
        yaxis: {
            show: false
        },
        legend: {
            position: 'right'
        },
        colors: ['#3246D3', '#00D0FF', '#ee3158', '#ffa800', '#008000'],
        plotOptions: {
            polarArea: {
                rings: {
                    strokeWidth: 0
                },
                spokes: {
                    strokeWidth: 0
                },
            }
        },
    };

    var chart = new ApexCharts(document.querySelector("#chart432"), options);
    chart.render();


    // Slim scrolling

    $('.inner-user-div').slimScroll({
        height: '283px'
    });

    $('.inner-user-div2').slimScroll({
        height: '298px'
    });

    $('.inner-user-div3').slimScroll({
        height: '200px'
    });

    $('.inner-user-div4').slimScroll({
        height: '432px'
    });

    var datepaginator = function() {
        return {
            init: function() {
                $("#paginator1").datepaginator()
            }
        }
    }();
    jQuery(document).ready(function() {
        datepaginator.init()
    });




}); // End of use strict
