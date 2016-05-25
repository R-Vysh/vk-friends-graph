package kyiv.rvysh.vkfriends.web.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kyiv.rvysh.vkfriends.graphalgorithms.evaluation.EvaluationService;
import kyiv.rvysh.vkfriends.utils.Pair;

@RequestMapping("/evaluate")
public class EvaluationWS {

	private EvaluationService service;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Double> evaluate(@RequestBody Pair<double[], double[]> result) {
		return new ResponseEntity<>(service.evaluateNMI(result.getFirst(), result.getSecond()),
				HttpStatus.OK);
	}

	public void setService(EvaluationService service) {
		this.service = service;
	}
}
