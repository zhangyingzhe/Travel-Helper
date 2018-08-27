var server_port = 9004;
//changing "localhost" to "0.0.0.0" for AWS EC2 config
var server_ip = '0.0.0.0';
var http = require("http");
const https = require('https');
var express=require("express");
var cors = require('cors');
var gmap_API_key = "AIzaSyBGRZTJ8BVZdyd53mKdQuosvK_wQpwJ-do";
const yelp = require('yelp-fusion');
const apiKey = 'i_uAXf4dVmFGfV_-WXLVmDqt1VkRdl1D6-who1gxUvbcwo70xoVXU-AfkJ7NOkKH78nNPGGsN7zutwJAc42nYlpZ0QidvzP8ZoydYeXispirLy5S6n6DG53NsRi5WnYx';
const yelpClient = yelp.client(apiKey);
var app=express();

app.use(cors());


app.get('/nearby_search', function(req,res){
	console.log(req.query);
	var distance = req.query.distance;
	var type = req.query.category;
	var keyword = req.query.keyword;
	var loca = req.query.location;
	var resJSON = {};
	if(typeof(req.query.needGetGeo) === 'undefined'){
		resJSON['start_point'] = loca;	
		var google_nearby_search = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ loca+"&radius="+distance+"&type="+type+"&keyword="+keyword+"&key="+gmap_API_key;
		console.log("google_nearby_search:" + google_nearby_search);
				
		https.get(google_nearby_search, function(res2){
			var body="";
			res2.on('data', function(d){
				body += d;
			});
			res2.on('end', function(){

				var parsed = JSON.parse(body);
				resJSON['results'] = parsed.results;
				resJSON['next_page_token'] = parsed.next_page_token;
				res.send(JSON.stringify(resJSON));
			});
		});	
	}
	else{
		var get_geo ="https://maps.googleapis.com/maps/api/geocode/json?address=" + loca + "&key=" + gmap_API_key;

		https.get(get_geo, function(res1){
			var body="";
			res1.on('data', function(d){body += d});
			res1.on('end', function(){
				var parsed = JSON.parse(body);
				if(parsed.results.length == 0){
					resJSON['start_point'] = "";
					resJSON['results'] = [];
					res.send(JSON.stringify(resJSON));
				}
				else{

					loca = parsed.results[0].geometry.location.lat + "," + parsed.results[0].geometry.location.lng;

					resJSON['start_point'] = loca;	
					var google_nearby_search = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ loca+"&radius="+distance+"&type="+type+"&keyword="+keyword+"&key="+gmap_API_key;
					console.log("google_nearby_search:" + google_nearby_search);
							
					https.get(google_nearby_search, function(res2){
						var body="";
						res2.on('data', function(d){
							body += d;
						});
						res2.on('end', function(){

							var parsed = JSON.parse(body);
							resJSON['results'] = parsed.results;
							resJSON['next_page_token'] = parsed.next_page_token;
							res.send(JSON.stringify(resJSON));
						});
					});	
				}
			});
		});
	}
});

app.get('/next_page',function(req,res){
	var url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken="+ req.query.token +"&key="+gmap_API_key;
	https.get(url, function(res1){
		var body="";
		res1.on('data', function(data){
			body += data;
		});
		res1.on('end', function(){
			var parsed = JSON.parse(body);
			console.log(JSON.stringify(parsed));
			if(parsed.results.length == 0){
			}
			else{
				res.send(body);
			}
		});
	});
});

app.get('/place_detail',function(req,res){
	console.log("place_detail");
	var url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+ req.query.placeid +"&key="+gmap_API_key;
	console.log(url);
	https.get(url, function(res1){
		var body="";
		res1.on('data', function(data){
			body += data;
		});
		res1.on('end', function(){
			var parsed = JSON.parse(body);
			console.log("get back");
			res.send(body);
		});
	});
});

app.get('/direction',function(req,res){
	console.log("direction");
	var url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "&mode=" + req.query.mode +"&origin=" + req.query.origin + "&destination=" + req.query.destination +
                "&key=" + gmap_API_key;
    console.log(url);
    https.get(url, function(res1){
    	var body = "";
    	res1.on('data',function(data){
    		body += data;
    	});
    	res1.on('end',function(){
    		res.send(body);
    	});
    });
});


app.get('/yelp_review',function(req,res){

	name = req.query.name;
	address = req.query.address;
	console.log(name + " " +address);
	str = address.split(',');
	len = str.length;
	st = str[len-2].split(' ');
	state = st[st.length-2];

	var searchRequest = {
	  name: name,
	  state: state,
	  country: 'US'
	};

	if(len - 4 >= 0) searchRequest['address1'] = str[len - 4];
	searchRequest['city'] = str[len-3];
	console.log(searchRequest);
	
	yelpClient.businessMatch('best', searchRequest).then(response => {
		console.log(response.jsonBody);
		if(response.jsonBody.businesses.length == 0){
			res.send('{}');
		}
		else{
			yelpClient.reviews(response.jsonBody.businesses[0].id).then(res2 => {
			  console.log(res2.jsonBody);
			  res.send(JSON.stringify(res2.jsonBody));
			}).catch(e => {
			  console.log(e);
			});
		}
	}).catch(e => {
	  console.log(e);
	});


});

function NearByRequest(google_nearby_search, res){
	https.get(google_nearby_search, function(res2){
			var body="";
			res2.on('data', function(d){
				body += d;
			});
			res2.on('end', function(){

				var parsed = JSON.parse(body);
				console.log(parsed);
				resJSON['results'] = parsed.results;
				resJSON['next_page_token'] = parsed.next_page_token;
				res.send(JSON.stringify(resJSON));
			});
		});	
}

app.use(function (req, res) {
	res.sendFile(__dirname + "/public/hw8.html");
});


var server = app.listen( process.env.PORT || "3000" , function () {
    var host = server.address().address;
    var port = server.address().port;
    console.log("App listening at http://%s:%s", host, port)
});


//var server = app.listen( server_port,server_ip, function () {
 //   var host = server.address().address;
 //   var port = server.address().port;
 //   console.log("App listening at http://%s:%s", host, port)
//});