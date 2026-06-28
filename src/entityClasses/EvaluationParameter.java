package entityClasses;

/*******
 * <p> Title: EvaluationParameter Class </p>
 *
 * <p> Description: This EvaluationParameter class represents a rubric parameter used to
 *  evaluate student discussion performance. Staff can create, read, update, and delete
 *  these parameters. All parameters' weights should sum to 1.0. </p>
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
	private double weight;     // 0.0-1.0; all parameters' weights should sum to 1.0
	private String createdBy;

	/*****
	 * <p> Method: EvaluationParameter(String name, String description, double maxScore,
	 *  double weight, String createdBy) </p>
	 *
	 * <p> Description: This constructor is used to establish EvaluationParameter entity
	 *  objects. </p>
	 *
	 * @param name specifies the name of this evaluation parameter
	 *
	 * @param description specifies the description of this evaluation parameter
	 *
	 * @param maxScore specifies the maximum score achievable for this parameter
	 *
	 * @param weight specifies the weight of this parameter (0.0-1.0); all weights sum to 1.0
	 *
	 * @param createdBy specifies the username of the staff member who created this parameter
	 *
	 */
	public EvaluationParameter(String name, String description, double maxScore,
			double weight, String createdBy) {
		this.name = name;
		this.description = description;
		this.maxScore = maxScore;
		this.weight = weight;
		this.createdBy = createdBy;
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
	public void setName(String name) { this.name = name; }


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
	public void setDescription(String description) { this.description = description; }


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
	public void setMaxScore(double maxScore) { this.maxScore = maxScore; }


	/*****
	 * <p> Method: double getWeight() </p>
	 *
	 * <p> Description: This getter returns the Weight. </p>
	 *
	 * @return a double of the Weight (0.0-1.0)
	 *
	 */
	public double getWeight() { return weight; }


	/*****
	 * <p> Method: void setWeight(double weight) </p>
	 *
	 * <p> Description: This setter defines the Weight attribute. </p>
	 *
	 * @param weight specifies the new weight for this evaluation parameter (0.0-1.0)
	 *
	 */
	public void setWeight(double weight) { this.weight = weight; }


	/*****
	 * <p> Method: String getCreatedBy() </p>
	 *
	 * <p> Description: This getter returns the CreatedBy username. </p>
	 *
	 * @return a String of the username who created this evaluation parameter
	 *
	 */
	public String getCreatedBy() { return createdBy; }
}
