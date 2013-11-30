
var state = {};

function initApp() {
   console.log("initApp");
   if (!redirectPage()) {
      $('#nav-login-persona').click(navigateLoginPersona);
      $('#nav-logout-persona').click(navigateLogoutPersona);
      initPersona();
   }
}

function redirectPage() {
   console.log("redicrectDocument " + window.location.protocol);
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

function navigateLoginPersona() {
    console.log("login persona");
    navigator.id.request();
}

function navigateLogoutPersona() {
    console.log("logout persona");
    navigator.id.logout();
}

function initPersona() {
    navigator.id.watch({
        loggedInUser: state.currentUser,
        onlogin: function(assertion) {
            console.log("onlogin");
            $.ajax({ 
                type: 'POST',    
                url: '/loginPersona',
                data: {
                    assertion: assertion
                },
                success: function(res, status, xhr) {
                    console.log("success");
                    console.log(res);
                    state.currentUser = res.email;
                    processLoginPersonaRes(res);
                },
                error: function(xhr, status, err) {
                    console.log("error");
                    processLoginPersonaError(error);
                }
            });
        },
        onlogout: function() {
            console.log("onlogout");
            state.currentUser = null;
            $.ajax({ 
                type: 'POST',    
                url: '/logoutPersona',
                success: function(res, status, xhr) {
                    console.log("success");
                    processLogout(res);
                },
                error: function(xhr, status, err) {
                    console.log("error");
                    processLogoutPersonaError(error);
                }
            });
        }
    });
}

function processLoginPersonaRes(res) {
    console.log("processLoginPersona", res.email);
    if (res.email !== null) {
    }
}

function processLoginPersonaError(err) {
    console.log("processLoginPersonaError", err);   
}

function processLogoutPersonaRes(res) {
    console.log("processLogoutPersona", res);   
}

function processLogoutPersonaError(err) {
    console.log("processLogoutPersonaError", err);   
}
