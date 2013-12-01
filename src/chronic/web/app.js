
var state = {};

function initApp() {
   console.log("initApp");
   if (!redirectPage()) {
      initPersona();
      $('#App-nav-login').click(navLoginPersona);
      $('#App-nav-logout').click(navLogoutPersona);
   }
}

function redirectPage() {
   console.log("redirectPage", window.location.origin);
   if (window.location.protocol === "https:") {
      return false;
   }
   var host = location.host;
   var index = location.host.indexOf(':');
   if (index > 0) {
      host = location.host.substring(0, index) + ':8443';
   }
   window.location = "https://" + host + location.pathname + location.search + location.hash;
   console.log(window.location);
   return true;
}

function navLoginPersona() {
   console.log("navLoginPersona");
   navigator.id.request();
}

function navLogoutPersona() {
   if (state.username !== null) {
      console.log("navLogoutPersona", state.username);
      navigator.id.logout();
   }
}

function initPersona() {
   navigator.id.watch({
      loggedInUser: null,
      onlogin: function(assertion) {
         console.log("onlogin");
         $.ajax({
            type: 'POST',
            url: '/app/LoginPersona',
            data: {
               assertion: assertion
            },
            success: function(res, status, xhr) {
               console.log("onlogin success", res);
               if (res.email && res.label) {
                  state.persona = res;
                  $('.App-label-username').text(res.label);
                  $('.App-view-loggedout').hide()
                  $('.App-view-loggedin').removeClass('hide');
                  $('.App-view-loggedin').show();
               }
            },
            error: function(xhr, status, err) {
               console.log("onlogin error", err);
            }
         });
      },
      onlogout: function() {
         console.log("onlogout", state.persona);
         if (state.persona && state.persona.email) {
            $.ajax({
               type: 'POST',
               url: '/app/LogoutPersona',
               data: {
                  email: state.persona.email
               },
               success: function(res, status, xhr) {
                  console.log("onlogout success", res);
                  state.persona = null;
                  $('.App-label-username').text('');
                  $('.App-view-loggedin').hide()
                  $('.App-view-loggedout').removeClass('hide');
                  $('.App-view-loggedout').show();
               },
               error: function(xhr, status, err) {
                  console.log("onlogout error", err);
               }
            });
         }
      }
   });
}

