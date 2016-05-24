function all() {
	var $body = $("body");
	var $cyContainer = $('#cy');
	var $userIdInput = $('#user-id-input');
	var $buildGraphButton = $('#graph-button');
	var $findCliquesButton = $('#cliques-button');
	var $findCommunitiesButton = $('#communities-button');
	var $communitiesParameterInput = $('#communities-param');
	var $communitiesMethod = $('#communities-method');
	var $loadFromVkButton = $('#load-friends-button');
	var $infoBlock = $('#info');
	var $cliquesList = $('#info ul');
	var cy;
	var graphResponse;
	var cliques = [];
	
	var ajaxVkFriends = function(userID) {
		return $.ajax({
			type : "POST",
			url : "/friends/save/" + userID,
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxFriends = function(userID) {
		return $.ajax({
			type : "GET",
			url : "/friends/get-graph/" + userID,
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};

	var ajaxClosestFriends = function(userID) {
		return $.ajax({
			type : "GET",
			url : "/friends/get-closest-friends/" + userID + "/10",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxClosestPeople = function(userID) {
		return $.ajax({
			type : "GET",
			url : "/friends/get-closest-people/" + userID + "/10",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxCliques = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/cliques",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxCPMCommunities = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/cpm",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};

	var ajaxGNCommunities = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/gn/" + $communitiesParameterInput.val(),
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxTLCommunities = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/topleaders/" + $communitiesParameterInput.val(),
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxLPCommunities1 = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/lp1",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxLPCommunities2 = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/lp2",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxLPCommunities3 = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/lp3/" + $communitiesParameterInput.val(),
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxCWCommunities = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/cw",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var ajaxMarkovCommunities = function() {
		return $.ajax({
			type : "POST",
			data : JSON.stringify(graphResponse),
			url : "/graph/communities/markov",
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};
	
	var defaultStyle = [{
			selector : 'node',
			style : {
				'width' : '8px',
				'height' : '8px',
				'label' : 'data(mutualFriends)'
			}
		}, {
			selector : 'edge',
			style : {
				'width' : 1,
				'line-color' : '#ccc',
				'target-arrow-color' : '#ccc',
				'target-arrow-shape' : 'none'
			}
		}, {
			selector : ":active",
			style : {
				"overlay-color" : "black",
				"overlay-padding" : 3,
				"overlay-opacity" : 0.2
			}
		}, {
			selector : ".closest-people",
			style : {
				"background-color" : "red"
			}
		}, {
			selector : ".closest-friends",
			style : {
				"background-color" : "black"
			}
		}, {
			selector : ".clique",
			style : {
				"background-color" : "yellow"
			}
		}
	];

	var defaultLayout = 'spread';

	var methodMapping = {
		girvanNewman: ajaxGNCommunities,
		cliquePercolation: ajaxCPMCommunities,
		topLeaders: ajaxTLCommunities,
		labelPropagation1: ajaxLPCommunities1,
		labelPropagation2: ajaxLPCommunities2,
		labelPropagation3: ajaxLPCommunities3,
		chineseWhisperer: ajaxCWCommunities,
		markov: ajaxMarkovCommunities
	};
	
	$loadFromVkButton.click(function () {
		var userId = $userIdInput.val();
		ajaxVkFriends(userId);
	});
	
	$findCliquesButton.click(function () {
		$.when(ajaxCliques()).done(
			function (responseRaw) {
				$infoBlock.show();
				$cliquesList.empty();
				cliques = [];
				for (var i = 0; i < responseRaw.length; i++) {
					$cliquesList.append("<li>" + i + "</li>");
					var clique = [];
					for(var j = 0; j < responseRaw[i].length; j++) {
						clique.push(responseRaw[i][j].uid);
					}
					cliques.push(clique);
				}
				var $cliquesLi = $("#info ul li");
				$cliquesLi.hover(
					function() {
						var nodes = cy.elements("node");
						var index = parseInt($(this).text());
						var data = cliques[index];
						for (var i = 0; i < data.length; i++) {
							for(var j = 0; j < nodes.length; j++) {
								if (nodes[j].data().uid == data[i]) {
									nodes[j].addClass("clique");
								}
							}
						}
					}, 
					function() {
						var nodes = cy.elements("node");
						for(var j = 0; j < nodes.length; j++) {
							nodes[j].removeClass("clique");
						}
					}
				);
		});
	});
	
	$findCommunitiesButton.click(function() {
		var methodAjax = methodMapping[$communitiesMethod.val()];
		$.when(methodAjax()).done(
			function (responseRaw) {
				drawCommunities(responseRaw);
		});
	});
	
	$buildGraphButton.click(function () {
		var userId = $userIdInput.val();
		$.when(ajaxFriends(userId), ajaxClosestFriends(userId), ajaxClosestPeople(userId)).done(
			function (responseRaw, responseRaw2, responseRaw3) {
			console.log("Response received");
			graphResponse = responseRaw[0];
			var response = responseRaw[0];
			var response2 = responseRaw2[0];
			var response3 = responseRaw3[0];
			var cytoData = prepareGraphData(response, response2, response3);
			console.log("Building graph...");
			buildGraph(cytoData);
		});
	});
	
	function drawCommunities(response) {
		var nodes = cy.elements("node");
		for (var i = 0; i < 100; i++) {
			for (var j = 0; j < nodes.length; j++) {
				nodes[j].removeClass("community-" + i);
			}
		}
		var newStyle = defaultStyle;
		for (var i = 0; i < response.length; i++) {
			var style = {
				selector : ".community-" + i,
				style : {
					"background-color" : getRandomColor()
				}
			};
			newStyle.push(style);
			for (var j = 0; j < response[i].length; j++) {
				for(var k = 0; k < nodes.length; k++) {
					if (nodes[k].data().uid == response[i][j].uid) {
						nodes[k].addClass("community-" + i);
					}
				}
			}
		}
		cy.style(newStyle);
		resizeElements();
	}
	
	function prepareGraphData(friends, closestFriends, closestPeople) {
		var nodes = [];
		var edges = [];
		for (var i = 0; i < friends.nodes.length; i++) {
			var node = {};
			node.group = 'nodes';
			node.data = friends.nodes[i].properties;
			node.data.id = friends.nodes[i].id;
			nodes.push(node);
		}
		for (var i = 0; i < friends.relationships.length; i++) {
			var edge = {};
			edge.group = 'edges';
			edge.data = friends.relationships[i].properties;
			edge.data.id = friends.relationships[i].id;
			edge.data.source = friends.relationships[i].startNode;
			edge.data.target = friends.relationships[i].endNode;
			edges.push(edge);
		}
		for (var person in closestFriends) {
			var personObj = JSON.parse(person);
			var visited = false;
			for (var i = 0; i < nodes.length; i++) {
				if (nodes[i].data.uid == personObj.uid) {
					nodes[i].data.mutualFriends = closestFriends[person];
					nodes[i].classes = 'closest-friends';
					visited = true;
				}
			}
		}
		for (var person in closestPeople) {
			var personObj = JSON.parse(person);
			var node = {};
			node.group = 'nodes';
			node.data = personObj;
			node.data.mutualFriends = closestPeople[person];
			node.classes = 'closest-people';
			nodes.push(node);
		}
		var cytoData = nodes.concat(edges);
		return cytoData;
	};

	function buildGraph(graphData) {
		cy = cytoscape({
				container : $cyContainer,
				elements : graphData,
				style : defaultStyle,
				wheelSensitivity : 0.2
			});
		var layoutParams = getLayout(defaultLayout, null);
		var layout = cy.makeLayout(layoutParams);
		layout.run();
		cy.on('click', 'node', function (evt) {
			var info = this.data();
			this.qtip({
				content : {
					title : {
						text : "<a href='https://vk.com/id" + info.uid
						 + "' target='_blank'>" + info.first_name + ' '
						 + info.last_name + "</a>"
					},
					text : "<a href='https://vk.com/id" + info.uid
					 + "' target='_blank'><img src='" + info.photo_50
					 + "'/></a>"
				},
				position : {
					my : 'top center',
					at : 'bottom center'
				},
				style : {
					classes : 'qtip-bootstrap',
				}
			});
		});
	};

	function getLayout(name, opts) {
		var defaultParams = {
			cola : {
				name : 'cola',
				nodeSpacing : 5,
				edgeLengthVal : 45,
				animate : true,
				randomize : false,
				maxSimulationTime : 1500,
				randomize : false
				// edgeLength: function(e) { return edgeLengthVal/e.data('weight');
				// }
			},
			random : {
				name : 'random'
			},
			spread : {
				name : 'spread',
				fit : true, // Reset viewport to fit default simulationBounds
				minDist : 10, // Minimum distance between nodes
				padding : 5, // Padding
			},
			coseBilkent : {
				name : 'cose-bilkent'
			}
		};
		var params = defaultParams[name];
		for (var i in opts) {
			params[i] = opts[i];
		}
		return params;
	}

	function resizeElements() {
		$infoBlock.height(window.innerHeight - 40);
		$cyContainer.width(window.innerWidth - 20 - $infoBlock.width());
		$cyContainer.height(window.innerHeight - 40);
		
	}

	function getRandomColor() {
		return "#"+((1<<24)*Math.random()|0).toString(16);	
	}
	
	$(document).keypress(function (e) {
		if (e.which == 13) {
			$('#graph-button').click();
		}
	});

	$(window).resize(function () {
		resizeElements();
	});
	
	$(document).on({
	    ajaxStart: function() { $body.addClass("loading"); },
	    ajaxStop: function() { $body.removeClass("loading"); }    
	});

	resizeElements();
}

$(document).ready(all);
