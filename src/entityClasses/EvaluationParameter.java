package entityClasses;

/*******
 * <p> Title: EvaluationParameter Class </p>
 *
 * <p> Description: This EvaluationParameter class represents a rubric parameter used to
 *  evaluate student discussion performance. Staff can create, read, update, and delete
 *  these parameters. Weight is relative(1-10), not a percentage. A parameter's contribution to a student's final
 *  score is computed at scoring time as its weight divided by the sum of all active parameter's weights. </p>
 *
 * <p> Copyright: Elena Mudrakova © 2026 </p>
 *
 * @author Elena Mudrakova
 *
 */

public class EvaluationParameter {
	/*
	 * These are the private attributes for this entity object
	 */
	private int    paramID;
	private String name;
	private String description;
	private double maxScore;
	private double weight;     // relative weight, 1-10

	/*****
	 * <p> Method: EvaluationParameter(String name, String description, double maxScore,
	 *  double weight) </p>
	 *
	 * <p> Description: This constructor is used to establish EvaluationParameter entity
	 *  objects. Handles validation of parameters. </p>
	 *
	 * @param name specifies the name of this evaluation parameter, must not be empty
	 *
	 * @param description specifies the description of this evaluation parameter, must be at least 40 chars
	 *
	 * @param maxScore specifies the maximum score achievable for this parameter (1-100)
	 *
	 * @param weight specifies the relative weight of this parameter (1-10)
	 * 
	 * @throws IllegalArgumentException if name is empty, maxScore is out of range, weight is out of range,
	 * or description is too short.
	 * 
	 * @see tests.EvaluationParameterCrudTest#testCreateValidParameter()
	 * @see tests.EvaluationParameterCrudTest#testCreateRejectEmptyName()
	 * @see tests.EvaluationParameterCrudTest#testCreateRejectNullName()
	 * @see tests.EvaluationParameterCrudTest#testMaxScoreLowerBoundaryValid()
	 * @see tests.EvaluationParameterCrudTest#testMaxScoreLowerBoundaryInvalid()
	 * @see tests.EvaluationParameterCrudTest#testMaxScoreUpperBoundaryValid()
	 * @see tests.EvaluationParameterCrudTest#testMaxScoreUpperBoundaryInvalid()
	 * @see tests.EvaluationParameterCrudTest#testWeightLowerBoundaryValid()
	 * @see tests.EvaluationParameterCrudTest#testWeightLowerBoundaryInvalid()
 	 * @see tests.EvaluationParameterCrudTest#testWeightUpperBoundaryValid()
 	 * @see tests.EvaluationParameterCrudTest#testWeightUpperBoundaryInvalid()
 	 * @see tests.EvaluationParameterCrudTest#testDescriptionBoundaryValid()
 	 * @see tests.EvaluationParameterCrudTest#testDescriptionBoundaryInvalid()
 	 * @see tests.EvaluationParameterCrudTest#testUniqueIDs()
	 *
	 */
	public EvaluationParameter(String name, String description, double maxScore,
			double weight) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter name must not be empty.");
		}
		if (maxScore < 1 || maxScore > 100) {
			throw new IllegalArgumentException("Max score must be between 1 and 100 inclusive.");
		}
		if (weight < 1 || weight > 10) {
			throw new IllegalArgumentException("Weight must be between 1 and 10 inclusive.");
		}
		if (description == null || description.length() < 40) {
			throw new IllegalArgumentException("Description must be at least 40 characters.");
		}
		
		this.name = name;
		this.description = description;
		this.maxScore = maxScore;
		this.weight = weight;
	}


	/*****
	 * <p> Method: int getParamID() </p>
	 *
	 * <p> Description: This getter returns the ParamID. </p>
	 *
	 * @return an int of the ParamID
	 *
	 */
	public int getParamID() { return paramID; }


	/*****
	 * <p> Method: void setParamID(int id) </p>
	 *
	 * <p> Description: This setter defines the ParamID attribute. </p>
	 *
	 * @param id specifies the ParamID assigned to this parameter by the database
	 *
	 */
	public void setParamID(int id) { paramID = id; }


	/*****
	 * <p> Method: String getName() </p>
	 *
	 * <p> Description: This getter returns the Name. </p>
	 *
	 * @return a String of the Name
	 *
	 */
	public String getName() { return name; }


	/*****
	 * <p> Method: void setName(String name) </p>
	 *
	 * <p> Description: This setter defines the Name attribute. </p>
	 *
	 * @param name specifies the new name for this evaluation parameter
	 *
	 */
	public void setName(String name) { 
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter name must not be empty.");
		}
		this.name = name; 
	}

	/*****
	 * <p> Method: String getDescription() </p>
	 *
	 * <p> Description: This getter returns the Description. </p>
	 *
	 * @return a String of the Description
	 *
	 */
	public String getDescription() { return description; }


	/*****
	 * <p> Method: void setDescription(String description) </p>
	 *
	 * <p> Description: This setter defines the Description attribute. </p>
	 *
	 * @param description specifies the new description for this evaluation parameter
	 *
	 */
	public void setDescription(String description) { 
		if (description == null || description.length() < 40) {
			throw new IllegalArgumentException("Description must be at least 40 characters.");
		}
		this.description = description; }


	/*****
	 * <p> Method: double getMaxScore() </p>
	 *
	 * <p> Description: This getter returns the MaxScore. </p>
	 *
	 * @return a double of the MaxScore
	 *
	 */
	public double getMaxScore() { return maxScore; }


	/*****
	 * <p> Method: void setMaxScore(double maxScore) </p>
	 *
	 * <p> Description: This setter defines the MaxScore attribute. </p>
	 *
	 * @param maxScore specifies the new maximum score for this evaluation parameter
	 *
	 */
	public void setMaxScore(double maxScore) { 
		if (maxScore < 1 || maxScore > 100) {
			throw new IllegalArgumentException("Max score must be between 1 and 100 inclusive.");
		}
		this.maxScore = maxScore; 
	}


	/*****
	 * <p> Method: double getWeight() </p>
	 *
	 * <p> Description: This getter returns the Weight. </p>
	 *
	 * @return a double of the Weight (1-10)
	 *
	 */
	public double getWeight() { return weight; }


	/*****
	 * <p> Method: void setWeight(double weight) </p>
	 *
	 * <p> Description: This setter defines the Weight attribute. </p>
	 *
	 * @param weight specifies the new weight for this evaluation parameter (1-10)
	 *
	 */
	public void setWeight(double weight) {	
		if (weight < 1 || weight > 10) {
		throw new IllegalArgumentException("Weight must be between 1 and 10 inclusive.");
		}
		this.weight = weight; 
	}	

}
