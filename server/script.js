//header("access-control-allow-origin: *");
var app = angular.module('travel',['ngSanitize','ngAnimate']);
app.controller('goSearch', function($scope, $http){
    $scope.favoriteTab = false;
    $scope.resultTab = false;
    $scope.detailTab = false;
    $scope.process = false;
    $scope.selectedLoc = true;
    $scope.categories = [
        {display: "default", search: "default"},
        {display: "Airport", search: "airport"},
        {display: "Amusement Park", search: "amusementPark"},
        {display: "Aquarium", search: "aquarium"},
        {display: "Art Gallery", search: "artGallery"},
        {display: "Backery", search: "backery"},
        {display: "Bar", search: "bar"},
        {display: "Beauty Salon", search: "beautySalon"},
        {display: "Boling Alley", search: "bolingAlley"},
        {display: "Bus Station", search: "busStation"},
        {display: "Cafe", search: "cafe"},
        {display: "Campground", search: "campground"},
        {display: "Car Rental", search: "carRental"},
        {display: "Casino", search: "casino"},
        {display: "Lodging", search: "lodging"},
        {display: "Movie Theater", search: "movieTheater"},
        {display: "Museum", search: "museum"},
        {display: "Night Club", search: "nightClub"},
        {display: "Park", search: "park"},
        {display: "Parking", search: "parking"},
        {display: "Restaurant", search: "restaurant"},
        {display: "Shopping Mall", search: "shoppingMall"},
        {display: "Stadium", search: "stadium"},
        {display: "Subway Station", search: "subwayStation"},
        {display: "Taxi Stand", search: "taxiStand"},
        {display: "Train Station", search: "trainStation"},
        {display: "Transit Station", search: "transitStation"},
        {display: "Travel Agency", search: "travelAgency"},
        {display: "Zoo", search: "zoo"}
    ];
    $scope.traffic_method=["DRIVING", "BICYCLING", "TRANSIT", "WALKING"];
    $scope.lat = undefined;
    $scope.lon = undefined;
    $scope.distance = 10;
    $scope.offset = 0;
    $scope.data=[];
    $scope.detailData=[];
    $scope.favoriteData = [];
    $scope.reviewYelp = [];
    $scope.resultData = [];
    $scope.curDetail = -1;
    $scope.pageToken= undefined;
    $scope.showStreet = true;
    $scope.FAVORITE = "";
    $scope.goSearch = function() {
        var locUrl;
        $scope.showProcess();
        if($scope.location == 'other'){
            $scope.start_point = $scope.place;
        }else{
            $scope.start_point = "Your Location";
        }
        if ($scope.location == "other") {
            locUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
            locUrl += $scope.place;
            locUrl += "&key=AIzaSyAOunfFzF-SKgiujodINlKnepIU8Eh9TNk"
            $http({
                method: 'GET',
                url: locUrl
            }).then(function successCallback(response) {
                $scope.lat = response.data.results[0].geometry.location.lat;
                $scope.lon = response.data.results[0].geometry.location.lng;
                $scope.loadSearch();
            }, function errorCallback(response) {
                $scope.resultStat = 404;
                $scope.showResult();
            });
        } else {
            locUrl = "http://ip-api.com/json";
            $http({
                method: 'GET',
                url: locUrl
            }).then(function successCallback(response) {
                $scope.lat = response.data.lat;
                $scope.lon = response.data.lon;
                $scope.loadSearch();
            }, function errorCallback(response) {
                $scope.resultStat = 404;
                $scope.showResult();
            });
        }
    }
    $scope.loadSearch = function(){
        //debug
        var url = "http://localhost:63342/P8_2/hw8.php?&keyword=" + $scope.keyword + "&location=" + $scope.lat + ",";
        url += $scope.lon + "&radius=" + $scope.distance + "&type=" + $scope.category;
        //aws
        //var url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?keyword=" + $scope.keyword+ "&location=";
        //url += $scope.lat + "," + $scope.lon + "&radius=" + $scope.distance + "&type=" + $scope.category;
        console.log(url);
        $http({
            method: 'GET',
            url: url
        }).then(function successCallback(response){
            $scope.pageToken  = response.data.next_page_token;
            $scope.resultData = response.data.results;
            $scope.data = $scope.resultData.slice(0, $scope.resultData.length);
            $scope.showResult();
        }, function errorCallback(response) {
            $scope.resultStat = 404;
        });
    }
    $scope.next = function(){
        $scope.offset += 20;
        if($scope.offset < $scope.resultData.length) {
            $scope.data = $scope.resultData.slice($scope.offset, Math.min($scope.offset + 20, $scope.resultData.length));
        }else{
            $scope.showProcess();
            var url = "http://localhost:63342/P8_2/hw8.php?&keyword=" + $scope.keyword + "&location=" + $scope.lat + ",";
            url += $scope.lon + "&radius=" + $scope.distance + "&type=" + $scope.category + "&pagetoken=" + $scope.pageToken;
            //aws
            //var url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?keyword=" + $scope.keyword + "&location=";
            //url += $scope.lat + "," + $scope.lon + "&radius=" + $scope.distance + "&type=" + $scope.category + "&pagetoken=" + $scope.pageToken;
            $http({
                method: 'GET',
                url: url
            }).then(function successCallback(response) {
                $scope.pageToken  = response.data.next_page_token;
                $scope.data = response.data.results;
                $scope.resultData = $scope.resultData.concat($scope.data);
                $scope.showResult();
            }, function errorCallback(response) {
                alert("nextPage");
            });
        }
    }
    $scope.prev = function(){
        $scope.offset = $scope.offset - 20;
        $scope.data = $scope.resultData.slice($scope.offset, Math.min($scope.offset + 20, $scope.resultData.length));
    }
    $scope.twitterhref= function(){
        var url = "https://twitter.com/intent/tweet?text=Check%20out%20";
        url += $scope.detailData.name +".%20Locate%20at%20" + $scope.detailData.formatted_address;
        url += ".%20Website%20:%20" + $scope.detailData.url + "%20%23TravelAndEntertainmentSearch";
        return url;
    }
    $scope.detail = function(index, place_id, vicinity){
        $scope.curPlaceId = place_id;
        $scope.showDetail();
        var map_id = document.getElementById("mmaapp");
        var map = new google.maps.Map(map_id, {
            center: {lat: $scope.lat, lng: $scope.lon},
            zoom: 14
        });
        var service = new google.maps.places.PlacesService(map);
        service.getDetails({
            placeId: place_id
        }, function (place, status) {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                $scope.$apply(function(){
                    $scope.curDetail = index;
                    $scope.detailData = place;
                    //initAutocomplete(document.getElementById('start_point'));
                });
                marker = new google.maps.Marker({
                    position: {lat: place.geometry.location.lat(), lng: place.geometry.location.lng()},
                    map: map
                });
                $scope.loadYelp(vicinity);
            }
        });
    }
    $scope.loadYelp = function(vicinity){
        var url = "http://localhost:63342/P8_2/hw8.php?&yelp&location=" + vicinity;
        //var url = "http://csci571-php-hw8.us-east-2.elasticbeanstalk.com/hw8.php?yelp&location=" + vicinity;
        $http({
            method: 'GET',
            url: url
        }).then(function successCallback(response){
            $scope.reviewYelp = response.data;
        }, function errorCallback(response) {
            alert("Yelp Review ERR");
        });
    }
    $scope.goBack = function(){
        if($scope.section == "Search") {
            $scope.resultTab = true;
            $scope.detailTab = false;
        }else{
            $scope.favoriteTab = true;
            $scope.detailTab = false;
        }
    }
    $scope.streetView = function(){
        $scope.showStreet = false;
        var pos = {lat: $scope.detailData.geometry.location.lat(), lng: $scope.detailData.geometry.location.lng()};
        var map_id = document.getElementById("mmaapp");
        var map = new google.maps.Map(map_id, {
            center: pos,
            zoom: 14
        });
        var panorama = new google.maps.StreetViewPanorama(
            document.getElementById('mmaapp'), {
                position: pos,
                pov: {
                    heading: 34,
                    pitch: 10
                }
            });
        map.setStreetView(panorama);
    }
    $scope.mapView = function(){
        $scope.showStreet = true;
        var map_id = document.getElementById("mmaapp");
        var map = new google.maps.Map(map_id, {
            center: {lat: 34.0266, lng: -118.2831},
            zoom: 14
        });
        marker = new google.maps.Marker({
            position: {lat: $scope.detailData.geometry.location.lat(), lng: $scope.detailData.geometry.location.lng()},
            map: map
        });
    }
    $scope.getDirection = function() {
        var origin;
        if ($scope.start_point == "Your Location" || $scope.start_point == $scope.place) {
            origin = {lat: $scope.lat, lng: $scope.lon};
            $scope.loadDirection(origin);
        } else {
            var locUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=";
            locUrl += $scope.start_point;
            locUrl += "&key=AIzaSyAOunfFzF-SKgiujodINlKnepIU8Eh9TNk"
            $http({
                method: 'GET',
                url: locUrl
            }).then(function successCallback(response) {
                var lat = response.data.results[0].geometry.location.lat;
                var lon = response.data.results[0].geometry.location.lng;
                origin = {lat: lat, lng: lon};
                $scope.loadDirection(origin);
            }, function errorCallback(response) {
                alert(3);
            });
        }
    }
    $scope.loadDirection = function(origin){
        var directionsService = new google.maps.DirectionsService;
        directionsDisplay = new google.maps.DirectionsRenderer;
        var map_id = document.getElementById("mmaapp");
        var map = new google.maps.Map(map_id, {
            center: origin,
            zoom: 14
        });
        directionsDisplay.setMap(map);
        directionsDisplay.setPanel(document.getElementById("panelmap"));
        directionsService.route({
            origin: origin,
            destination: {lat: $scope.detailData.geometry.location.lat(), lng: $scope.detailData.geometry.location.lng()},
            travelMode: $scope.traffic,
            provideRouteAlternatives:true
        }, function(response, status) {
            if (status === 'OK') {
                directionsDisplay.setDirections(response);
            }
        });
    }
    $scope.opening_hours = function(){
        if($scope.detailData != undefined && $scope.detailData.opening_hours != undefined &&
            $scope.detailData.opening_hours.open_now != undefined){
            if($scope.detailData.opening_hours.open_now == false){
                return "Close now";
            }else{
                var day = (new Date().getDay() + 6) % 7;
                var str = $scope.detailData.opening_hours.weekday_text[day];
                return "Open now: " + str.substring(str.indexOf(' '), str.length);
            }
        }
        return '';
    }
    $scope.getTime = function(UNIX_timestamp){
        a = new Date(UNIX_timestamp * 1000);
        year = a.getFullYear();
        month = "0"+a.getMonth();
        date = "0"+a.getDate();
        hour ="0"+a.getHours();
        min = "0"+a.getMinutes();
        sec = "0"+a.getSeconds();
        time = year+'-'+ month.substr(-2) +'-'+ date.substr(-2) + ' ' + hour.substr(-2) + ':' + min.substr(-2) + ':' + sec.substr(-2);
        return time;
    }
    $scope.clear = function(){
        $scope.keyword = undefined;
        $scope.place = undefined;
        $scope.favoriteTab = false;
        $scope.resultTab = false;
        $scope.detailTab = false;
        $scope.process = false;
        $scope.selectedLoc = true;
        $scope.category= $scope.categories[0].display;
        $scope.lat = undefined;
        $scope.lon = undefined;
        $scope.distance = 10;
        $scope.offset = 0;
        $scope.data=[];
        $scope.detailData=[];
        $scope.favoriteData = [];
        $scope.reviewYelp = [];
        $scope.resultData = [];
        $scope.curDetail = -1;
        $scope.pageToken= undefined;
        $scope.showStreet = true;
    }
    $scope.showProcess = function(){
        $scope.resultTab = false;
        $scope.favoriteTab = false;
        $scope.detailTab = false;
        $scope.process = true;
    }
    $scope.showFavorite = function(){
        $scope.resultTab = false;
        $scope.favoriteTab = true;
        $scope.detailTab = false;
        $scope.process = false;
        $scope.section = "Favorite";
        //$scope.favoriteData = $scope.getToLocalStorage();
    }
    $scope.showResult = function(){
        $scope.resultTab = true;
        $scope.favoriteTab = false;
        $scope.detailTab = false;
        $scope.process = false;
        $scope.section = "Search";
    }
    $scope.showDetail = function(){
        $scope.resultTab = false;
        $scope.favoriteTab = false;
        $scope.detailTab = true;
        $scope.process = false;
    }
    $scope.saveFavorite = function(icon, name, vicinity, place_id){
        $scope.favoriteData.push([icon, name, vicinity, place_id]);
        $scope.saveToLocalStorage($scope.favoriteData);
    }
    $scope.deleteFavorite = function(place_id){
        var index = $scope.favoriteData.findIndex(function (array) {
            return array[3] === place_id;
        })

        if (index >= 0) {
            $scope.favoriteData.splice(index, 1);
            $scope.saveToLocalStorage($scope.favoriteData);
        }
    }
    $scope.checkFavorite = function(place_id){
        var index = $scope.favoriteData.findIndex(function (array) {
            return array[3] === place_id;
        })
        return index >= 0;
    }
    $scope.saveToLocalStorage = function(data){
        localStorage.setItem($scope.ƒ, JSON.stringify(data));
    }

    $scope.getToLocalStorage = function(){
        return JSON.parse(localStorage.getItem($scope.FAVORITE)) || [];
    }
});
var autocomplete;
var componentForm = {
    street_number: 'short_name',
    route: 'long_name',
    locality: 'long_name',
    administrative_area_level_1: 'short_name',
    country: 'long_name',
    postal_code: 'short_name'
};

function initAutocomplete() {
    // Create the autocomplete object, restricting the search to geographical
    // location types.
    autocomplete = new google.maps.places.Autocomplete(
        /** @type {!HTMLInputElement} */(document.getElementById('autocomplete')),
        {types: ['geocode']});

    // When the user selects an address from the dropdown, populate the address
    // fields in the form.
    autocomplete.addListener('place_changed', fillInAddress);
}

function fillInAddress() {
    // Get the place details from the autocomplete object.
    var place = autocomplete.getPlace();

    for (var component in componentForm) {
        document.getElementById(component).value = '';
        document.getElementById(component).disabled = false;
    }

    // Get each component of the address from the place details
    // and fill the corresponding field on the form.
    for (var i = 0; i < place.address_components.length; i++) {
        var addressType = place.address_components[i].types[0];
        if (componentForm[addressType]) {
            var val = place.address_components[i][componentForm[addressType]];
            document.getElementById(addressType).value = val;
        }
    }
}

// Bias the autocomplete object to the user's geographical location,
// as supplied by the browser's 'navigator.geolocation' object.
function geolocate() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            var geolocation = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };
            var circle = new google.maps.Circle({
                center: geolocation,
                radius: position.coords.accuracy
            });
            autocomplete.setBounds(circle.getBounds());
        });
    }
}