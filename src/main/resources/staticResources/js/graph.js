function all() {
	
	
	
  $('#graph-button').click(
	function() {
	  var friends = [];
	  var user = {};
	  var userId = $('#user-id-input').val();
	  
	  function ajaxFriends() {
		return $.ajax({
		  type : "GET",
		  url : "/friends/get-graph/" + userId,
		  timeout : 30000,
		  headers : {
		    "Accept" : "application/json; charset=utf-8",
			"Content-Type" : "application/json; charset=utf-8"
		  }
		});
	  }

	  $.when(ajaxFriends()).done(
		function(response) {
			var cytoData = [];
			for (var i = 0; i < response.nodes.length; i++) {
			  var node = {};
			  node.data = response.nodes[i].properties;
			  node.data.id = response.nodes[i].id;
			  node.group = 'nodes';
			  cytoData.push(node);
			}
			for (var i = 0; i < response.relationships.length; i++) {
			  var edge = {};
			  edge.data = response.relationships[i].properties;
			  edge.data.id = response.relationships[i].id;
			  edge.data.source = response.relationships[i].startNode;
			  edge.data.target = response.relationships[i].endNode;
			  edge.group = 'edges';
			  cytoData.push(edge);
			}
			var cy = cytoscape({
			  container: $('#cy'),
			  elements: cytoData,
			  style: [
			    {
			      selector: 'node',
			      style: {
			        'width': '8px',
			        'height': '8px'
			      }
			    },
			    {
			      selector: 'edge',
			      style: {
			        'width': 1,
			        'line-color': '#ccc',
			        'target-arrow-color': '#ccc',
			        'target-arrow-shape': 'none'
			      }
			    },
			    {
			      selector: ":active",
				  style: {
					"overlay-color": "black",
					"overlay-padding": 3,
					"overlay-opacity": 0.2
				  }
				}
			  ]
		    });
			var layoutParams = getLayout('spread', null);
			var layout = cy.makeLayout(layoutParams);
			layout.run();
			cy.on('mouseover', 'node', function(evt) {
//			  this.css({
//				    'pointer': 'hand'
//			  });
			  var info = this.data();
			  this.qtip({
				content: { 
				  title: {
				    text: "<a href='https://vk.com/id" + info.uid + "' target='_blank'>" + info.first_name + ' ' + info.last_name + "</a>"
				  },
				  text: "<a href='https://vk.com/id" + info.uid + "' target='_blank'><img src='" + info.photo_50 + "'/></a>"
				},
				position: {
				  my: 'top center',
				  at: 'bottom center'
				},
				style: {
				  classes: 'qtip-bootstrap',
				}
			  });
			});
		});
    });
  
    function getLayout(name, opts) {
      var defaultParams = {
    	cola: {
    	  name: 'cola',
    	  nodeSpacing: 5,
    	  edgeLengthVal: 45,
    	  animate: true,
    	  randomize: false,
    	  maxSimulationTime: 1500,
    	  randomize: false
//    	  edgeLength: function(e) { return edgeLengthVal/e.data('weight'); }
    	},
    	random: {
    	  name: 'random'
    	},
    	spread: {
    	  name: 'spread',
//    	  animate: true,
    	  fit: true, // Reset viewport to fit default simulationBounds
    	  minDist: 20, // Minimum distance between nodes
    	  padding: 10, // Padding
//    	  expandingFactor: -1.0, 
//    	  maxFruchtermanReingoldIterations: 50, // Maximum number of initial force-directed iterations
//    	  maxExpandIterations: 4, // Maximum number of expanding iterations
//    	  boundingBox: undefined, // Constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
//    	  randomize: true // uses random initial node positions on true
    	},
    	coseBilkent: {
      	  name: 'cose-bilkent'
      	}
      };
      var params = defaultParams[name];
	  for (var i in opts) {
	    params[i] = opts[i];
	  }
	  return params;
	};
	
  
	$(document).keypress(function(e) {
		if (e.which == 13) {
			$('#graph-button').click();
		}
	});
}

$(document).ready(all);