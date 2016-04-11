function all() {
	var cyContainer = $('#cy');
	var userIdInput = $('#user-id-input');
	var buildGraphButton = $('#graph-button');

	var ajaxFriends = function (userID) {
		return $.ajax({
			type : "GET",
			url : "/friends/get-graph/" + userID,
			headers : {
				"Accept" : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			}
		});
	};

	var ajaxClosestPeople = function (userID) {
		return $.ajax({
			type : "GET",
			url : "/friends/get-closest-people/" + userID + "/10",
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
				"background-color" : "black"
			}
		}, {
			selector : ".recommended-people",
			style : {
				"background-color" : "red"
			}
		}
	];

	var defaultLayout = 'spread';
	
	buildGraphButton.click(function () {
		var userId = userIdInput.val();
		$.when(ajaxFriends(userId), ajaxClosestPeople(userId)).done(
			function (responseRaw, responseRaw2) {
			console.log("Response received");
			var response = responseRaw[0];
			var response2 = responseRaw2[0];
			var cytoData = prepareGraphData(response, response2);
			console.log("Building graph...");
			buildGraph(cytoData);
		});
	});

	function prepareGraphData(friends, closestPeople) {
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
		for (var person in closestPeople) {
			var personObj = JSON.parse(person);
			var visited = false;
			for (var i = 0; i < nodes.length; i++) {
				if (nodes[i].data.uid == personObj.uid) {
					nodes[i].data.mutualFriends = closestPeople[person];
					nodes[i].classes = 'closest-people';
					visited = true;
				}
			}
			if (!visited) {
				var node = {};
				node.group = 'nodes';
				node.data = personObj;
				node.data.mutualFriends = closestPeople[person];
				node.classes = 'recommended-people';
				nodes.push(node);
			}
		}
		var cytoData = nodes.concat(edges);
		return cytoData;
	};

	function buildGraph(graphData) {
		var cy = cytoscape({
				container : cyContainer,
				elements : graphData,
				style : defaultStyle
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
	};

	function resizeCy() {
		cyContainer.width(window.innerWidth - 20);
		cyContainer.height(window.innerHeight - 40);
	};

	$(document).keypress(function (e) {
		if (e.which == 13) {
			$('#graph-button').click();
		}
	});

	$(window).resize(function () {
		resizeCy();
	});

	resizeCy();
}

$(document).ready(all);
