var chatApp = angular.module('chatApp',[]);

chatApp.factory('getSockJS', function () {
    return {
        sock: function(){
            var sock = new SockJS('http://localhost:8080/integracion');
            return sock;
        }
    }
});

chatApp.controller('chatApp.message', ['$scope', 'getSockJS', '$http', '$rootScope',
    function($scope, getSockJS, $http, $rootScope) {
        var sockjs = getSockJS.sock();

        //Traemos las sesiones actuales
        $http.get("http://localhost:8080/sesions/findAll").then(function(response) {
            $scope.sesiones = response.data;
        });

        //Mensaje a enviar
        $scope.messageSend = "";
        //Sesion actual
        $scope.idSesion  = "";
        //Todas las sesion
        $scope.sesiones = [];
        //Mensajes por usuario
        $scope.messageUser = [];
        $scope.messageFilter = [];
        $scope.userSelected = {};

        sockjs.onopen = function(e) {
            console.log("CONECTANDO A WS");
        };

        sockjs.onmessage = function(e) {
            var data = JSON.parse(e.data)
            console.log("MESSAGE = " + JSON.stringify(data))

            switch (data.type){
                case "SESION_ID":
                    $scope.idSesion = data.body;
                    console.log("SESION_ID = " + $scope.idSesion)
                    break;
                case "NEW_USER":
                    $scope.sesiones.push({
                        id: data.body
                    });
                    break;
                case "NEW_MESSAGE":
                    $scope.messageUser.push(data.body);
                    console.log("Mensaje nuevo ")
                    console.log($scope.messageUser);

                    Push.create("Mensaje Nuevo de " + data.body.id, {
                        body: data.body.message,
                        timeout: 4000,
                        onClick: function () {
                            window.focus();
                            this.close();
                        }
                    });
                    updateMessages();
                    break;
            }

            $scope.$apply();
        };

        sockjs.onclose = function() {
            console.log('close');
        };
        
        $scope.selectSesionSend = function (sesion) {
            console.log(sesion);
            $scope.userSelected = sesion;
            updateMessages();
        }

        $scope.sendMessage = function (message) {
            sockjs.send(JSON.stringify({
                id: $scope.userSelected.id,
                message: message
            }));

            $scope.messageUser.push({
                id: $scope.idSesion,
                message: message,
                date: new Date().toString()
            });

            updateMessages();
        }

        var updateMessages = function () {
            $scope.messageFilter = [];
            console.log($scope.messageUser)

            for (data  in $scope.messageUser){
                console.log(data)
                if($scope.messageUser[data].id == $scope.userSelected.id){
                    $scope.messageFilter.push($scope.messageUser[data]);
                } else if ($scope.messageUser[data].id == $scope.idSesion){
                    $scope.messageFilter.push($scope.messageUser[data]);
                }
            }
            console.log($scope.messageFilter)
        }
    }]);