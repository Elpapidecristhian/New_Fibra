$(function () {
  var INDEX = 0;

  $("#chat-submit").click(function (e) {
    e.preventDefault();
    var msg = $("#chat-input").val();
    if (msg.trim() === '') return false;

    generate_message(msg, 'self');

    fetch('/vecino/api/chatbot', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ pregunta: msg })
    })
        .then(response => response.json())
        .then(data => {
          generate_message(data.respuestaBot, 'user');
        })
        .catch(error => {
          generate_message("Hubo un error al contactar al asistente.", 'user');
          console.error("Error chatbot:", error);
        });
  });

  function generate_message(msg, type) {
    INDEX++;
    var isUser = (type === 'user');
    var avatar = isUser ? "/images/avatar/chatbot.png" : "/images/avatar/3.jpg";
    var nombre = isUser ? "San Miguelito" : "Tú";

    var str = "";
    str += `<div id='cm-msg-${INDEX}' class="chat-msg ${type}">`;
    str += `  <div class="d-flex align-items-center ${isUser ? '' : 'justify-content-end'}">`;
    if (isUser) {
      str += `    <span class="msg-avatar"><img src="${avatar}" class="avatar avatar-lg"></span>`;
    }
    str += `    <div class="mx-10">`;
    str += `      <a href="#" class="text-dark hover-primary fw-bold">${nombre}</a>`;
    str += `      <p class="text-muted font-size-12 mb-0">Ahora</p>`;
    str += `    </div>`;
    if (!isUser) {
      str += `    <span class="msg-avatar"><img src="${avatar}" class="avatar avatar-lg"></span>`;
    }
    str += `  </div>`;
    str += `  <div class="cm-msg-text">${msg}</div>`;
    str += `</div>`;

    $(".chat-logs").append(str);
    $(`#cm-msg-${INDEX}`).hide().fadeIn(300);
    if (type === 'self') {
      $("#chat-input").val('');
    }
    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight }, 1000);
  }

  // Este aún no lo usas dinámicamente, pero lo dejamos por si quieres botones interactivos
  function generate_button_message(texto, buttons) {
    INDEX++;
    var str = "";
    str += `<div id='cm-msg-${INDEX}' class="chat-msg user">`;
    str += `  <div class="d-flex align-items-center">`;
    str += `    <span class="msg-avatar"><img src="/images/avatar/chatbot.png" class="avatar avatar-lg"></span>`;
    str += `    <div class="mx-10">`;
    str += `      <a href="#" class="text-dark hover-primary fw-bold">San Miguelito</a>`;
    str += `      <p class="text-muted font-size-12 mb-0">Ahora</p>`;
    str += `    </div>`;
    str += `  </div>`;
    str += `  <div class="cm-msg-text">${texto}</div>`;
    str += `</div>`;
    $(".chat-logs").append(str);
    $(`#cm-msg-${INDEX}`).hide().fadeIn(300);
    $(".chat-logs").stop().animate({ scrollTop: $(".chat-logs")[0].scrollHeight }, 1000);
  }

  $(document).delegate(".chat-btn", "click", function () {
    var value = $(this).attr("chat-value");
    var name = $(this).html();
    $("#chat-input").attr("disabled", false);
    generate_message(name, 'self');
  });

});
