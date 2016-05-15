package kyiv.rvysh.vkfriends.web.services;

import java.util.Collection;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.services.GraphService;

@RequestMapping("/graph")
public class GraphWS {

	private GraphService<PersonInfo> service;

	@RequestMapping(value = "/cliques", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Collection<Set<PersonInfo>>> findCliques(@RequestBody Neo4jGraph<PersonInfo> graph) {
		return new ResponseEntity<>(service.findCliques(graph), HttpStatus.OK);
	}

	@RequestMapping(value = "/communities/cpm", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Collection<Set<PersonInfo>>> findCommunitiesCPM(@RequestBody Neo4jGraph<PersonInfo> graph) {
		return new ResponseEntity<>(service.findCommunitiesCpm(graph), HttpStatus.OK);
	}

	@RequestMapping(value = "/communities/gn/{edgesToRemove}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Collection<Set<PersonInfo>>> findCommunitiesGN(
			@PathVariable("edgesToRemove") Integer numOfEdges, @RequestBody Neo4jGraph<PersonInfo> graph) {
		return new ResponseEntity<>(service.findCommunitiesGn(graph, numOfEdges), HttpStatus.OK);
	}

	@RequestMapping(value = "/communities/topleaders/{clusters}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Collection<Set<PersonInfo>>> findCommunitiesTopLeaders(
			@PathVariable("clusters") Integer numOfClusters, @RequestBody Neo4jGraph<PersonInfo> graph) {
		return new ResponseEntity<>(service.findCommunitiesTopleaders(graph, numOfClusters), HttpStatus.OK);
	}

	public void setService(GraphService<PersonInfo> service) {
		this.service = service;
	}
}
