package kyiv.rvysh.vkfriends.web.services;

import java.util.Collection;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	
	@RequestMapping(value = "/communities", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Collection<Set<PersonInfo>>> findCommunities(@RequestBody Neo4jGraph<PersonInfo> graph) {
		return new ResponseEntity<>(service.findCommunities(graph), HttpStatus.OK);
	}

	public void setService(GraphService<PersonInfo> service) {
		this.service = service;
	}
}
