package entityClasses;

import java.util.ArrayList;
import java.util.List;

/*******
 * <p> Title: EvaluationParameterList Class </p>
 *
 * <p> Description: This EvaluationParameterList class supports storing all current
 *  EvaluationParameter objects in the system, as well as retrieving parameters by
 *  ID or name. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 */

public class EvaluationParameterList {
	/*
	 * These are the private attributes for this entity object
	 */
	private List<EvaluationParameter> parameters;

	/*****
	 * <p> Method: EvaluationParameterList() </p>
	 *
	 * <p> Description: This constructor establishes an empty EvaluationParameterList,
	 *  ready to have EvaluationParameter objects added to it. </p>
	 *
	 */
	public EvaluationParameterList() {
		this.parameters = new ArrayList<EvaluationParameter>();
	}


	/*****
	 * <p> Method: void addParameter(EvaluationParameter p) </p>
	 *
	 * <p> Description: This method adds an EvaluationParameter object to the list. </p>
	 *
	 * @param param specifies the EvaluationParameter object to be added to the list
	 *
	 */
	public void addParameter(EvaluationParameter param) {
		parameters.add(param);
	}


	/*****
	 * <p> Method: List EvaluationParameter getAllParameters() </p>
	 *
	 * <p> Description: This getter returns the complete list of EvaluationParameter
	 *  objects. </p>
	 *
	 * @return a List of all EvaluationParameter objects currently stored
	 *
	 */
	public List<EvaluationParameter> getAllParameters() {
		return parameters;
	}


	/*****
	 * <p> Method: EvaluationParameter getParameterByID(int paramID) </p>
	 *
	 * <p> Description: This method returns the EvaluationParameter object matching
	 *  the specified paramID, or null if no such parameter exists. </p>
	 *
	 * @param paramID specifies the ID of the parameter to find
	 *
	 * @return the EvaluationParameter matching the specified paramID, or null if not found
	 *
	 */
	public EvaluationParameter getParameterByID(int paramID) {
		for (EvaluationParameter p : parameters) {
			if (p.getParamID() == paramID) {
				return p;
			}
		}
		return null;
	}


	/*****
	 * <p> Method: EvaluationParameter getParameterByName(String name) </p>
	 *
	 * <p> Description: This method returns the EvaluationParameter object matching
	 *  the specified name, or null if no such parameter exists. </p>
	 *
	 * @param name specifies the name of the parameter to find
	 *
	 * @return the EvaluationParameter matching the specified name, or null if not found
	 *
	 */
	public EvaluationParameter getParameterByName(String name) {
		for (EvaluationParameter p : parameters) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
}
