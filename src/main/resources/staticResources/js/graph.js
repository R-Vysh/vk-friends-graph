function all() {

  $('#graph-button').click(
	function() {
	  var friends = [];
	  var user = {};
	  var userId = $('#user-id-input').val();
      
	  function ajaxFriends() {
		return $.ajax({
		  type : "GET",
		  url : "/vk/list-friends/" + userId,
		  timeout : 5000,
		  headers : {
		    "Accept" : "application/json; charset=utf-8",
			"Content-Type" : "html/text; charset=utf-8"
		  }
		});
	  }

	  function ajaxUser() {
		return $.ajax({
		  type : "GET",
		  url : "/vk/info/" + userId,
		  timeout : 5000,
		  headers : {
			"Accept" : "application/json; charset=utf-8",
			"Content-Type" : "html/text; charset=utf-8"
		  }
		});
	  }

	  function loadPictures(listOfPeople, svg) {
        var defs = svg.append('svg:defs');
		for(var i = 0; i < listOfPeople.length; i++) {
	      defs.append("svg:pattern")
			.attr("id", "avatar" + listOfPeople[i].uid)
			.attr("width", 50)
			.attr("height", 50)
			.attr("patternUnits", "userSpaceOnUse")
			.append("svg:image")
			.attr("xlink:href", listOfPeople[i].photo_50)
			.attr("width", 50)
			.attr("height", 50)
			.attr("x", 0)
			.attr("y", 0);  
		}
	  }
	  
	  $.when(ajaxUser(), ajaxFriends()).done(
		function(a1, a2) {
		  user = a1[0];
		  friends = a2[0];
		  var width = window.innerWidth - 20;
		  var height = window.innerHeight - 80;
		  var color = d3.scale.category20();
		  var force = d3.layout.force().charge(-500).linkStrength(0.05).linkDistance(200).size([ width, height ]);
		  var svg = d3.select("body").append("svg").attr("width", width).attr("height", height);
		  friends.unshift(user);
		  loadPictures(friends, svg);
		  var dataToDraw = {};
		  dataToDraw.nodes = friends;
		  dataToDraw.links = [];
		  for (var i = 1; i < friends.length; i++) {
			var link = {
			  source: 0,
			  target: i,
			  value: 4
			};
		    dataToDraw.links.push(link);
		  }
		  force.nodes(dataToDraw.nodes).links(dataToDraw.links).start();
          var link = svg.selectAll(".link")
          	.data(dataToDraw.links).enter().append("line").attr("class", "link")
          	.style("stroke-width", function(d) {
			  return Math.sqrt(d.value);
			});
		  var node = svg.selectAll(".node")
		  	.data(dataToDraw.nodes)
		  	.enter()
		  	.append("circle")
		  	.attr("class", "node")
		  	.attr("r", 25)
		  	.style("fill", "#fff")
		  	.style("fill", function(d) {
		  	  return "url(#avatar" + d.uid + ")";
		  	});
          node.append("title").text(function(d) {
        	return d.first_name + " " + d.last_name;
		  });
          
          force.on("tick", function() {
			link.attr("x1", function(d) {
			  return d.source.x;
			}).attr("y1", function(d) {
			  return d.source.y;
			}).attr("x2", function(d) {
			  return d.target.x;
			}).attr("y2", function(d) {
			  return d.target.y;
			});
			node.attr("cx", function(d) {
			  return d.x;
			}).attr("cy", function(d) {
			  return d.y;
			});
		  });
		});
    });

	$(document).keypress(function(e) {
		if (e.which == 13) {
			$('#graph-button').click();
		}
	});
}

$(document).ready(all);