
var directionsDisplay;
var marker;
var placeSearch, autocomplete;
var end_point;
var start_point;
var from_text;

//var server_path = "http://localhost:3000/";
var server_path ="http://csci571yingzhez-envhw9.us-east-2.elasticbeanstalk.com/";
var current_location;

function initAutocomplete() {
        // Create the autocomplete object, restricting the search to geographical
        // location types.
        autocomplete = new google.maps.places.Autocomplete(
            /** @type {!HTMLInputElement} */(document.getElementById('location_text')),
            {types: ['geocode']});

        // When the user selects an address from the dropdown, populate the address
        // fields in the form.
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


function Reset(){
	place_detail = {};
	results_table_data = {pages:[], cur_page:-1, token:[]};
	favorite_table_data = {pages:[], cur_page:-1};
	review_data = {google_review:[],yelp_review:[]};
	$("#results-detail-button").attr('disabled',true);
  	$("#favorites-detail-button").attr('disabled',true);
	$('#search_form')[0].reset();
	$('#location_text').attr('disabled',true);

	$('#pills-results-tab').addClass('active');
	$('#pills-results-tab').addClass('show');
	$('#pills-results-tab').attr('aria-selected',true);
	$('#pills-results').addClass('active');
	$('#pills-results').addClass('show');
	$('#pills-favorites-tab').removeClass('active');
	$('#pills-favorites-tab').removeClass('show');
	$('#pills-favorites').removeClass('active');
	$('#pills-favorites').removeClass('show');
	$('#pills-tabContent').removeClass('ng-hide-remove');
	$('#pills-favorites-tab').attr('aria-selected',false);

	$('#detail-area').removeClass('ng-hide-remove');

	$('#nav-info-tab').addClass('active');
	$('#nav-info-tab').addClass('show');
	$('#nav-info').addClass('active');
	$('#nav-info').addClass('show');

	$('#nav-photos-tab').removeClass('active');
	$('#nav-photos-tab').removeClass('show');
	$('#nav-photos').removeClass('active');
	$('#nav-photos').removeClass('show');

	$('#nav-map-tab').removeClass('active');
	$('#nav-map-tab').removeClass('show');
	$('#nav-map').removeClass('active');
	$('#nav-map').removeClass('show');

	$('#nav-reviews-tab').removeClass('active');
	$('#nav-reviews-tab').removeClass('show');
	$('#nav-reviews').removeClass('active');
	$('#nav-reviews').removeClass('show');
	$("#location_text").removeClass("is-invalid");
	$("#keyword").removeClass("is-invalid");


}

function favorites2results(){
	$('#pills-results-tab').addClass('active');
	$('#pills-results-tab').addClass('show');
	$('#pills-results-tab').attr('aria-selected',true);
	$('#pills-results').addClass('active');
	$('#pills-results').addClass('show');
	$('#pills-favorites-tab').removeClass('active');
	$('#pills-favorites-tab').removeClass('show');
	$('#pills-favorites-tab').attr('aria-selected',false);

	$('#pills-favorites').removeClass('active');
	$('#pills-favorites').removeClass('show');
}


function fromLocationChange(){
	console.log("fromLocationChange");
	if($('#from2').is(':checked')){
		$('#location_text').attr('disabled',false);
		$('#search').attr('disabled',true);

	}
	else 
		$('#location_text').attr('disabled',true);
		$("#location_text").removeClass("is-invalid");

}



var map_autocomplete;
function mapAutocomplete(){
	 map_autocomplete = new google.maps.places.Autocomplete(
            /** @type {!HTMLInputElement} */(document.getElementById('map_from')),
            {types: ['geocode']});
}


      function map_geolocate() {
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
            map_autocomplete.setBounds(circle.getBounds());
          });
        }
      }

function getGeolocation(){
	$('#search').attr('disabled',true);

	$.get("http://ip-api.com/json",function(data){
		jsondoc = data;
		start_point = {lat: jsondoc.lat, lng: jsondoc.lon};
		current_location = jsondoc.lat+","+jsondoc.lon;
		console.log(start_point,current_location);
	});
}

var results_table_data = {pages:[], cur_page:-1, token:[]};


function SearchNearby(){
	results_table_data = {pages:[], cur_page:-1, token:[]};
	$('.progress').show();
	$('#pills-tabContent').removeClass('ng-hide-remove');
  	$('#detail-area').removeClass('ng-hide-remove');
  	$("#results-detail-button").attr('disabled',true);
  	$("#favorites-detail-button").attr('disabled',true);
  	place_detail = {};
  	favorites2results();
	var data = document.getElementById('search_form');
	var distance = 10 * 1609.344;
	if(data.distance.value)
		distance = data.distance.value * 1609.344;
	console.log(distance / 1609.344);
	var urls = server_path + "nearby_search?distance=" + distance.toString() + "&category=" + data.category.value + "&keyword=" + data.keyword.value + "&location=";
	if(data.from.value == "option1"){
		from_text = "Your location";
		urls += current_location;
	}
	else{
		from_text = data.location.value;
		urls += data.location.value + "&needGetGeo=true";
	}
	urls = encodeURI(urls);
	console.log("search nearby url " + urls);
	
	$.ajax({url:urls, async:true, success:function(data){
		console.log("results  :  "+data);
		data = JSON.parse(data);
		start_point = data.start_point;
		getSearchResult(data);
		
	}, error:function(){console.log('e')}});
	return false;
}

/*function getSearchResult(data){
	
	results = data.results;
	console.log("start_point " + start_point);
	console.log(results);
	var text = "";
	if(results.length == 0){
		$('#detail-button').hide();
		text += '<div class="alert alert-warning" role="alert" style="margin-top: 13%">No records.</div>';	
	}
	else{
		$('#detail-button').show();
		results_table_data.cur_page +=1;

		text ="<table class=\"table table-hover\">";
			text +="<thead><tr><th>\#</th><th>Category</th><th>Name</th><th>Address</th><th>Favorite</th><th>Detail</th></tr></thead>";
			text += "<tbody>";
			var No = 1;
			for(i in results){
				text += "<tr>"; 
				text += "<td>" + No + "</td>";
				No += 1;
				var store_data = {name:results[i].name,icon:results[i].icon,address:results[i].vicinity,place_id:results[i].place_id};
				store_data = JSON.stringify(store_data);
				store_data = encodeURI(store_data);
				text += "<td><img src=\"" + results[i].icon + "\" width=40px alt=\"\"></td>";
				text += "<td>" + results[i].name + "</td>";
				text += "<td>" + results[i].vicinity + "</td>";

				text += '<td><button type ="button" class="btn btn-light border" id= ' + store_data +  ' onclick = "favoritesStorage(this)">';
				if(localStorage.getItem(results[i].place_id)) text += '<i class="fa fa-star" style="color:yellow"></i>';
				else text += '<i class="fa fa-star-o"></i>';
				text +='</button></td>';


				text += '<td><button type="button" class="btn btn-light border" id =' + results[i].place_id + " onclick = \"GetPlaceDatail(this.id)\">" + " <span class=\"fa fa-chevron-right\"></span> </button></td>";
				text += "</tr>"; 
			}
			text += "</tbody></table>"
			text += '<nav aria-label="Page navigation example">\
						<div class="d-flex justify-content-center">\
						<ul class="pagination">';
		if(results_table_data.cur_page > 0){
			text += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick="getPreviousPage()">Previous</a></li>';
		}
		if(data.next_page_token){
			next_page_token = data.next_page_token;
			text += '<li class="page-item"><a class="page-link" href="javascript:void(0)" onclick= "getNextPage()"' + '>Next</a></li>';
		}
		text +="</ul></div></nav>";
		results_table_data.pages.push(text);
		results_table_data.token.push(data.next_page_token);
	}	
	$('.progress').hide();
	$("#pills-results").html(text);
	$('#pills-tabContent').show();
	return false;
}*/


//results_table_data = {pages:[], cur_page:-1, token:[]};
var SearchNearby_data_ready = false;
function getSearchResult(data){
	console.log('getSearchResult');
	results = data.results;
	if(results.length == 0){
	}
	else{
		$('#results-detail-button').show();
		results_table_data.cur_page +=1;
		var No = 1;
		page=[];
			for(i in results){
				obj = {};
				obj['idx'] = No ;
				No += 1;
				var store_data = {name:results[i].name,icon:results[i].icon,address:results[i].vicinity,place_id:results[i].place_id};
				store_data = JSON.stringify(store_data);
				store_data = encodeURI(store_data);
				obj['icon'] = results[i].icon ;
				obj['name'] = results[i].name ;
				obj['address'] =  results[i].vicinity;
				obj['store_data'] = store_data;
				obj['place_id'] = results[i].place_id ;
				page.push(obj);
				if(localStorage.getItem(results[i].place_id)){
					obj['is_fav'] = true;
				}else{
					obj['is_fav'] = false;
				}
			}
		if(data.next_page_token){
			results_table_data.token.push(data.next_page_token);
		}
		results_table_data.pages.push(page);
		
	}	
	$('.progress').hide();
	genResultPage();
	showList();
	return false;
}

var favorite_table_data = {pages:[], cur_page:-1};

function showFavoritesTable(){
	showList();
	var text = "";
	favorite_table_data = {pages:[], cur_page:-1};
	if(localStorage.length == 0){
		$('#favorites-detail-button').hide();
		text += '<div class="alert alert-warning" role="alert" style="margin-top: 13%">No records.</div>';
		$("#pills-favorites-area").html(text);
	}
	else{
		$('#favorites-detail-button').show();
		var idx = 0;	
		while(idx < localStorage.length){
			text ='<table class="table table-hover ">';
			text +="<thead><tr><th>\#</th><th>Category</th><th>Name</th><th>Address</th><th>Favorite</th><th>Details</th></tr></thead>";
			text += "<tbody>";
			for(var i = 0; i < 20; i++)
			{
				if(idx >= localStorage.length) break;
				record = localStorage.getItem(localStorage.key(idx));
				idx +=1;
				record = JSON.parse(record);
				if(place_detail.place_id && record.place_id == place_detail.place_id){
					 text += '<tr class="detail-block">'; 
				}
				else text += "<tr>"; 
				var j = i + 1;
				text += "<td>" + j + "</td>";
				text += "<td><img src=\"" + record.icon + "\" width=40px alt=\"\"></td>";
				text += "<td>" + record.name + "</td>";
				text += "<td>" + record.address + "</td>";

				text += '<td><button type ="button" class="btn btn-light border" onclick = "favoritesStorageDelete(\'' + record.place_id + '\')"><i class="fa fa-trash"></i></button></td>';

				text += '<td><button type="button" class="btn btn-light border" id =' + record.place_id + ' onclick = "GetPlaceDatail(this.id)">' + " <span class=\"fa fa-chevron-right\"></span> </button></td>";
				text += "</tr>"; 
			}
			text += "</tbody></table>";
			favorite_table_data.pages.push(text);			
		}
		favorite_table_data.cur_page = 0;
		$("#pills-favorites-area").html(favorite_table_data.pages[0]);

		favoritesPreNext();
	}
}

function favoritesPreNext(){
	//console.log('favoritesPreNext');
	var pn_text = '<nav aria-label="Page navigation example">\
						<div class="d-flex justify-content-center">\
						<ul class="pagination">';
	//console.log(favorite_table_data);
			if(favorite_table_data.cur_page > 0)
			{
				pn_text += '<li class="page-item"><button type="button" class="btn btn-light border" class="page-link" onclick="getPreviousFavoritePage()">Previous</button></li>';
			}
			if(favorite_table_data.cur_page > 0 && favorite_table_data.cur_page + 1 < favorite_table_data.pages.length){
				pn_text +='<li>&nbsp;&nbsp;&nbsp;&nbsp;</li>'
			}
			if(favorite_table_data.cur_page + 1 < favorite_table_data.pages.length)
			{
				pn_text += '<li class="page-item"><button type="button" class="btn btn-light border" class="page-link" onclick= "getNextFavoritePage()"' + '>&nbsp;&nbsp;&nbsp;Next&nbsp;&nbsp;&nbsp;</button></li>';
			}
			pn_text +="</ul></div></nav>";
		$("#favorites_pre_next").html(pn_text);
}

function getNextPage(){
	var cur = results_table_data.cur_page;
	//console.log(results_table_data.cur_page,results_table_data.token);
	if(cur + 1 >= results_table_data.pages.length){
		var urls = server_path + "next_page?token=" + results_table_data.token[cur];	
		console.log(urls);		
		$.ajax({url:urls, async: true, success:function(data){
			//console.log("results  :  "+data);
			data = JSON.parse(data);
			getSearchResult(data);
					
		}});
	}
	else{
		results_table_data.cur_page = results_table_data.cur_page + 1;
		genResultPage();
	}
}


function getPreviousPage(){
	var cur = results_table_data.cur_page;
	results_table_data.cur_page = cur - 1;
	genResultPage();
}


function getNextFavoritePage(){
	var cur = favorite_table_data.cur_page;
	favorite_table_data.cur_page = cur + 1;
	$("#pills-favorites-area").html(favorite_table_data.pages[cur + 1]);
			favoritesPreNext();


}


function getPreviousFavoritePage(){
	var cur = favorite_table_data.cur_page;
	$("#pills-favorites-area").html(favorite_table_data.pages[cur - 1]);
	favorite_table_data.cur_page = cur - 1;
			favoritesPreNext();

}


var PlaceName;
var photoNum;
var map;
function GetPlaceDatail(id){
	$('#pills-tabContent').removeClass('ng-hide-remove');
  	$('#detail-area').removeClass('ng-hide-remove');
  	$('.progress').show();
  	$('#nav-info-tab').addClass('active');
	$('#nav-info-tab').addClass('show');
	$('#nav-info').addClass('active');
	$('#nav-info').addClass('show');

	$('#nav-photos-tab').removeClass('active');
	$('#nav-photos-tab').removeClass('show');
	$('#nav-photos').removeClass('active');
	$('#nav-photos').removeClass('show');

	$('#nav-map-tab').removeClass('active');
	$('#nav-map-tab').removeClass('show');
	$('#nav-map').removeClass('active');
	$('#nav-map').removeClass('show');

	$('#nav-reviews-tab').removeClass('active');
	$('#nav-reviews-tab').removeClass('show');
	$('#nav-reviews').removeClass('active');
	$('#nav-reviews').removeClass('show');

	var placeid = id;
	//console.log(placeid);
	 map = new google.maps.Map(document.getElementById('map'), {
    zoom: 13
  });

	var request = {
  		placeId: placeid
	};
	service = new google.maps.places.PlacesService(map);
	service.getDetails(request, getPlaceDetailCallback);
}


var place_detail = {};
var place_detail_ready = false;
function getPlaceDetailCallback(place, status) {
	$('.progress').hide();
	place_detail = place;
	if (status == google.maps.places.PlacesServiceStatus.OK) {
  		//console.log(place);
  		var store_data = {name:place.name,icon:place.icon,address:place.formatted_address,place_id:place.place_id};
		store_data = JSON.stringify(store_data);
		store_data = encodeURI(store_data);
		place_detail['store_data'] = store_data;

  		var twitter_content = "Check out " + place.name + " located at " + place.formatted_address + ".Website:";
  		if(place.website){
  			twitter_content += place.website;
  		}else{
  			twitter_content += place.url;
  		}
  		twitter_content += "&hashtags=TravelAndEntertainmentSearch";
  		place_detail['twitter_content'] = encodeURI(twitter_content);

  		genDetailTitle();
  		showInfo(place);
  		showPhotos(place);
  		showMap(place);
  		showReviews(place);
  		mapAutocomplete();
  		$("#detail-button").attr('disabled',false);
  		$("#results-detail-button").attr('disabled',false);

  		$("#favorites-detail-button").attr('disabled',false);

  		showDetail();	
  }
}


function genDetailTitle(){
	var place = place_detail;
  		var text ="";
  		text += '<h3 style="text-align:center">' + place.name + '</h3>';
  		text += '<div class="d-flex">';
  		text += '<div class="mr-auto p-2"><button type="button" class="btn btn-light border" onclick="showListButton()"><span class="fa fa-chevron-left"></span>List</button></div>';
  		text += '<div class="p-2"><button type ="button" class="btn btn-light border" id= ' + place.store_data +  ' onclick = "favoritesStorage(this)">';
		if(localStorage.getItem(place.place_id)) text += '<i class="fa fa-star" style="color:#FFCC33"></i>';
		else text += '<i class="fa fa-star-o"></i>';
		
		text +='</button></div>';
  		text += '<div class="p-2"><img src="http://cs-server.usc.edu:45678/hw/hw8/images/Twitter.png" onclick=tweetCompose(this) style="align:right;width:40px" id= ' + place.twitter_content +'></div>';
  		text += '</div>';
  		$('#detail-title').html(text);
}


function genResultPage(){
	var text = "";

	console.log('genResultPage');
	var cur = results_table_data.cur_page;
	if(cur == -1 || cur >= results_table_data.pages.length){
		$('#results-detail-button').hide();
		text += '<div class="alert alert-warning" role="alert" style="margin-top: 13%">No records.</div>';
		$("#pills-results-area").html(text);
		$("#results_pre_next").html("");

		showList();	
		return;
	}
	var results = results_table_data.pages[cur];


		$('#detail-button').show();
		text += '<div class="table-responsive-sm">';
		text ="<table class=\"table table-hover \">";
			text +="<thead><tr><th>\#</th><th>Category</th><th>Name</th><th>Address</th><th>Favorite</th><th>Details</th></tr></thead>";
			text += "<tbody>";
			for(i in results){
				if(place_detail.place_id && results[i].place_id == place_detail.place_id){
					 text += '<tr class="detail-block">'; 
				}
				else text += "<tr>"; 
				text += "<td>" + results[i].idx + "</td>";
				text += "<td><img src=\"" + results[i].icon + "\" width=40px alt=\"\"></td>";
				text += "<td>" + results[i].name + "</td>";
				text += "<td>" + results[i].address + "</td>";

				text += '<td><button type ="button" class="btn btn-light border" id= ' + results[i].store_data +  ' onclick = "favoritesStorage(this)">';
				if(localStorage.getItem(results[i].place_id)) text += '<i class="fa fa-star" style="color:#FFCC33"></i>';
				else text += '<i class="fa fa-star-o"></i>';
				text +='</button></td>';
				text += '<td><button type="button" class="btn btn-light border" id =' + results[i].place_id + " onclick = \"GetPlaceDatail(this.id)\">" + " <span class=\"fa fa-chevron-right\"></span> </button></td>";
				text += "</tr>"; 
			}
			text += "</tbody></table></div>";
			var pn_text = '<nav aria-label="Page navigation example" id="results_pre_next">\
						<div class="d-flex justify-content-center">\
						<ul class="pagination">';
		if(results_table_data.cur_page > 0){
			pn_text += '<li class="page-item"><button type="button" class="btn btn-light border" class="page-link" onclick="getPreviousPage()">Previous</button></li>';
		}
		if(results_table_data.cur_page > 0 && results_table_data.token.length > cur){
				pn_text +='<li>&nbsp;&nbsp;&nbsp;&nbsp;</li>'
			}
		if(results_table_data.token.length > cur){
			pn_text += '<li class="page-item"><button type="button" class="btn btn-light border" class="page-link" onclick= "getNextPage()"' + '>&nbsp;&nbsp;&nbsp;Next&nbsp;&nbsp;&nbsp;</button></li>';
		}
		pn_text +="</ul></div></nav>";
	$("#pills-results-area").html(text);
	$("#results_pre_next").html(pn_text);

	showList();
}

function showDetail() {
	console.log("showDetail");
	$('#pills-tabContent').removeClass('ng-hide-remove');
  	$('#detail-area').addClass('ng-hide-remove');
  	genDetailTitle();

}

function showList(){
	console.log("showList");
     $('#pills-tabContent').addClass('ng-hide-remove');
  	$('#detail-area').removeClass('ng-hide-remove');
}

function showListButton(){
	console.log("showListButton");
	genResultPage();
	showFavoritesTable();
     $('#pills-tabContent').addClass('ng-hide-remove');
  	$('#detail-area').removeClass('ng-hide-remove');
}

function showResultsList(){
	console.log("showResultsList");
  	genResultPage();
}

function tweetCompose(what){
	window.open("https://twitter.com/intent/tweet?text=" +what.id ,"","width=400,height=400");
}

function showReviews(place){
	review_data = {google_review:[],yelp_review:[]};

	    googleReview(place);
	 
	    yelpReview(place);
}

function yelpReview(place){
	urls = server_path + 'yelp_review?name=' + place.name + "&address=" + place.formatted_address;
	urls = encodeURI(urls);
	console.log(urls);
	$.ajax({url:urls, async: true, success:function(data){
				//console.log("results  :  "+data);

				yelpReviewCallback(JSON.parse(data));
			}});
}

var review_data = {google_review:[],yelp_review:[]}

function yelpReviewCallback(data){
	//console.log("yelpReviewCallback   " + JSON.stringify(data));
	if(data.reviews){
		reviews = data.reviews;
	    for (var i = 0; i < reviews.length;i++){
		    x = {};
	   		if(reviews[i].user.image_url) x['photo_url'] =  reviews[i].user.image_url;
	    	x['author_name']= reviews[i].user.name ;

			x['rating'] = reviews[i].rating;
			x['stars'] = [];
			for(var j = 0; j < reviews[i].rating; j++){
				x['stars'].push(j);
			}
			//console.log(x.stars);
	    	x['time'] = reviews[i].time_created ;
	    	x['text'] =  reviews[i].text ;
	    	x['author_url'] = reviews[i].url;
	    	review_data.yelp_review.push(x);
	    }
	}
}

function googleReview(place){
	if(!place.reviews || place.reviews.length == 0) return;
	reviews = place.reviews;
	//console.log("GOOGLEREVIEW   " + JSON.stringify(reviews));
	for (var i = 0; i < reviews.length;i++){
	    x = {};
	   	if(reviews[i].profile_photo_url)
	   		x['photo_url'] =  reviews[i].profile_photo_url;
    	x['author_name']= reviews[i].author_name;
		x['rating'] = reviews[i].rating;
		x['stars'] = [];
		for(var j = 0; j < reviews[i].rating; j++){
			x['stars'].push(j);
		}
		//console.log(x.stars);
    	x['time'] = moment.unix(reviews[i].time).format("YYYY-M-DD hh:mm:ss");
    	x['text'] =  reviews[i].text ;
    		x['author_url'] = reviews[i].author_url;
    	review_data.google_review.push(x);
    }

}


var map_center;
function showMap(place){
	map_center = JSON.stringify(place.geometry.location);
	var text = '';
	$('#map_to').attr('value',place.formatted_address);
	$('#map_from').attr('value',from_text);
	//console.log("showMap:" + from_text)

	initMap();

}

function showPhotos(place){
	var text = '';
	if(!place.photos){
		text +=  '<div class="alert alert-warning" role="alert" style="margin-top: 13%"> No records.</div>';
		$('#nav-photos').html(text);
		return ;
	}
	var photos = place.photos;
	
	text += '<div class="d-inline d-sm-none">';
	for(var i = 0; i < photos.length; i+=1){
		url = photos[i].getUrl({'maxWidth': 3500, 'maxHeight': 3500});
		console.log(url);
	    text += '<div class="card">' + '<a href=" ' + url +  '" target="_blank"><img src="' + url + '" alt="" class="img-thumbnail" ></a></div>';
  	}
  	text += "</div>"; // for card-column
  	text += '<div class = "d-none d-sm-flex justify-content-between">';
  	//text += '<div class="card-columns" style="column-count:4">';
	for(var k = 0; k < 4; k++){
		text += '<div class="float-left" style="max-width:25%;min-width:25%">';
	for(var i = k; i < photos.length; i+=4){
		url = photos[i].getUrl({'maxWidth': 3500, 'maxHeight': 3500});
		console.log(i + ":" + url);
	    text += '<div class="card m-1 mb-2">' + '<a href=" ' + url +  '" target="_blank"><img src="' + url + '" alt="" class="img-thumbnail" ></a></div>';
  	}
  	text += '</div>';
  }
  	text +="</div>"
  	//text += "</div></div>"; // for card-column
  	$('#nav-photos').html(text);
}




function showInfo(place){

	var text = "";
	text += '<div id="info-table-area">';
	text += '<table class="table table-striped" id="info-table"><tbody>';
	
	if(place.formatted_address)
		text += "<tr><th>Address</th><td>" + place.formatted_address + "</td></tr>";
	if(place.international_phone_number) 
		text += "<tr><th>Phone Number</th><td>" + place.international_phone_number + "</td></tr>";
	if(place.price_level){
		text += "<tr><th>Price Level</th><td>" ;
		for(var i = 0; i < place.price_level;i++)
			text  += "$" ;
		text += "</td></tr>"; 
	}
	if(place.rating){

		text += "<tr><th>Rating</th><td>" + place.rating ;
		var rating = place.rating;
		text += '&nbsp;<div class="stars-outer"><div class="stars-inner"></div></div>';
		text += "</td></tr>";
	}
if(place.url){
	text += "<tr><th>Google Page</th><td>" + '<a href="' +place.url + '" target="_blank">' + place.url + "</a></td></tr>"; 
}
if(place.website){
	text += "<tr><th>Website</th><td>" + '<a href="' +place.website + '" target="_blank">'+ place.website + "</a></td></tr>"; 
}
if(place.opening_hours){
	console.log("open:" + JSON.stringify(place.opening_hours));
	text += "<tr><th>Hours</th><td>";
	var weekday_idx = moment().utcOffset(place.utc_offset).format('d') - 1;
	console.log("week day:" + weekday_idx);
	if(place.opening_hours.open_now){

		
		if(weekday_idx < 0) weekday_idx = 6;
		var open_hour = place.opening_hours.weekday_text[weekday_idx];
		var str = open_hour.split(':');
		str.splice(0,1);
		str = str.join(':');
		text +='Open now:' + str;

	}else{
		text += 'Closed';
	}		
	text += '<button type="button" class="btn btn-link" data-toggle="modal" data-target="#open-hour-modal">Daily open hours</button>';
	genOpenHourTable(place.opening_hours.weekday_text, weekday_idx);

	text += "</td></tr>";
}
	text += "</tbody></table>";
	text +="</div>";
	$('#nav-info').html(text);
	if(place.rating)
		$('.stars-inner').css('width', place.rating / 5 * 100 + "%");
}


function genOpenHourTable(weekday_text, idx=0){
	console.log(weekday_text );
	console.log("idx:" + idx);
	if(idx < 0) idx += 7;
	open_hour_body = "";
		open_hour_body += '<table class="table">';
		for(var i = 0; i < 7; i++){

			str = weekday_text[(idx % 7)].split(':');
			idx +=1;
			week = str.splice(0,1);
			str = str.join(':');
			if(i == 0)
				open_hour_body += '<tr class="font-weight-bold"><td>' + week + '</td><td>' + str + '</td></tr>';
			else
				open_hour_body += '<tr><td>' + week + '</td><td>' + str + '</td></tr>';

		}
		open_hour_body += '</table>';
		$('#open-hour-body').html(open_hour_body);
}

var cnt_store = 0;
function favoritesStorage(what){
	console.log("store");
	json_str = decodeURI(what.id);
	json = JSON.parse(json_str);
	key = json.place_id;
	jq = "#" + json_str;
	if(localStorage.getItem(key)){

	what.innerHTML = '<i class="fa fa-star-o"></i>';
		localStorage.removeItem(json.place_id);
	}else{
		what.innerHTML = '<i class="fa fa-star" style="color:#FFCC33"></i>';
		localStorage.setItem(json.place_id,json_str);
	}
	//console.log(localStorage);
}

function favoritesStorageDelete(key){
	localStorage.removeItem(key);
	showFavoritesTable();
}


function initMap() {
	var tmp = JSON.parse(map_center);
	if(directionsDisplay)
		directionsDisplay.setPanel(null);
	directionsDisplay = new google.maps.DirectionsRenderer();
	end_point = {lat:tmp.lat, lng:tmp.lng};
    map = new google.maps.Map(document.getElementById('map_area'), {
        zoom: 13,
        center: end_point,
        mapTypeControl:true,
    });
    marker = new google.maps.Marker({
        position: end_point,
        map: map
  	});
}

function findRoute(){    
	marker.setMap(null);  
	var directionsService = new google.maps.DirectionsService();
	var from = $('#map_from').val();
	var to = $('#map_to').val();
	var travel_mode = $('#map_travel_mode').val();
	console.log(travel_mode);
	directionsDisplay.setMap(null);  	
	directionsDisplay.setPanel(null);
  	directionsDisplay.setMap(map);  
  	directionsDisplay.setPanel(document.getElementById('directionsPanel'));

  	var request = {
        origin: start_point,
        destination: end_point,
        provideRouteAlternatives: true,
        travelMode: travel_mode,
  	};
  	if(from == "Your location" || from == "My location"){
  		request['origin'] = start_point;
  	}else{
  		request['origin'] = from;
  	}

  	directionsService.route(request, function(result, status) {
	    if (status == 'OK') {
	      	directionsDisplay.setDirections(result);
	    }
  	});
}


var showmap = false;
var current_id = "";

function displayMap(what){
	initMap();
}

function mapDisplayType(){
	console.log("mapDisplayType");
	if($('#map-display-type').attr('src').match('Pegman')){
		$('#map-display-type').attr('src','http://cs-server.usc.edu:45678/hw/hw8/images/Map.png');
		var tmp = JSON.parse(map_center);
		end_point = {lat:tmp.lat, lng:tmp.lng};
		var panorama = new google.maps.StreetViewPanorama(
            document.getElementById('map_area'), {
              position: end_point,
              pov: {
                heading: 34,
                pitch: 10
              }
            });
        map.setStreetView(panorama);
        	directionsDisplay.setPanel(null);

	}
	else{
		$('#map-display-type').attr('src','http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png');
		initMap();
	}
}

$(document).ready(function(){
    $("#get_directions").click(function(){
    	findRoute();
    });
      $("#map-display-type").click(function(){
    	mapDisplayType();
    });
      // $("#detail-button").click(function(){
    //	showDetail();
   // });

   $("#keyword").blur(function(){
   	var str = $("#keyword").val();
   		if(str.length == 0 || !str.match(/\w+/))
   		{
   			$("#keyword").addClass("is-invalid");
   			$('#search').attr('disabled',true);
   		}
   		else{
   			$("#keyword").removeClass("is-invalid");
   			$('#search').attr('disabled',false);
   		}
   });


   $("#location_text").blur(function(){

	   	if($('#from2').is(':checked')){
	   		var str = $("#location_text").val();
	   		if(str.length == 0 || !str.match(/\w+/))
	   		{
	   			$("#location_text").addClass("is-invalid");
	   			$('#search').attr('disabled',true);

	   		}
	   		else{
	   			$("#location_text").removeClass("is-invalid");
	   			$('#search').attr('disabled',false);
	   		}
	   	}
   });

});


var app = angular.module("myApp",['ngAnimate']);

app.run(function($rootScope){
	$rootScope.review_type = 'Google Reviews';
	$rootScope.sort_order = 'Default Order';
	$rootScope.review_data = review_data.google_review;
	$rootScope.sort_order = 'index_num';
	$rootScope.order_text = 'Default Order';
	$rootScope.place = place_detail;
	$rootScope.results_table_page = [];
	$rootScope.results_table_data = {};
	$rootScope.ngshow_pills_tabContent = false;
	$rootScope.test_choice = 'show';
	$google_review_switch = true;

	//console.log("scopr : " + $rootScope.review_data);
});


app.controller("myCtrl",function($scope,$timeout){
	$scope.changeReviewSource = function(x){
		$scope.review_data = review_data[x];
		$scope.google_review_switch = false;
		//console.log("changeReviewSource"  + $scope.google_review_switch);
		$timeout(function(){
			$scope.google_review_switch = true;
			//console.log("timeout"  + $scope.google_review_switch);
		},300);
	};
	$scope.changeReviewSourceTab = function(x){
		console.log('changeReviewSourceTab');
		$scope.review_data = review_data[x];
		$scope.google_review_switch = true;

	};
	$scope.changeResultsData = function(){
		$scope.results_table_data = results_table_data;
		for(p = 0; p < $scope.results_table_data.pages.length; p++){
			var page = $scope.results_table_data.pages[p];
			for(var i = 0; i < page.length;i++){
				//console.log($scope.results_table_data.pages[p][i].is_fav);
				if(localStorage.getItem(page[i].place_id)){
					console.log(i);
					$scope.results_table_data.pages[p][i].is_fav = true;
				}
				else{
					$scope.results_table_data.pages[p][i].is_fav = false;
				}
				//console.log("after:" + $scope.results_table_data.pages[p][i].is_fav);
			}
		}
		results_table_data = $scope.results_table_data;

		//console.log("changeResultsData ");
	}

	$scope.checkFav = function(x){
		if(localStorage.getItem(x)){
			return true;
		}else{
			return false;
		}
	}

	$scope.ngSearchNearby = function(){
		SearchNearby();
		$scope.results_table_data = results_table_data;
		console.log('ngSearchNearby');
		$('.progress').hide();

	}

	$scope.ng_GetPlaceDatail = function(x){
		$scope.ngshow_pills_tabContent = false;
		console.log("ng detail" + x);
		GetPlaceDatail(x);
		$scope.place = place_detail;
	}

	$scope.ng_getNextPage = function(){
		getNextPage();
		$scope.results_table_data = results_table_data;
	}
	$scope.ng_getPreviousPage = function(){
		getPreviousPage();
		$scope.results_table_data = results_table_data;
	}

	$scope.favoritesStorage = function(){
		json = $scope.place;
		key = json.place_id;
		if(localStorage.getItem(key)){

		//what.innerHTML = '<i class="fa fa-star-o"></i>';
		localStorage.removeItem(json.place_id);
		}else{
		//what.innerHTML = '<i class="fa fa-star" style="color:yellow"></i>';
		localStorage.setItem(json.place_id,decodeURI(json.store_data));
		}
		console.log("ng-click");
		$scope.place.is_fav = !$scope.place.is_fav;
		place_detail = $scope.place;
		console.log($scope.place.is_fav,place_detail.is_fav );
	}

	$scope.checkreadydata = function(){
			$scope.place = place_detail;
		}

});



