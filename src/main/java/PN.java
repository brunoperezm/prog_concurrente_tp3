import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PN {
	private Array2DRowRealMatrix mIncidenceMatrix;
	private Array2DRowRealMatrix mMarking;

	enum Transitions {
		ARRIVAL_RATE_1(0), // arrival_rate

		// CPU 1

		START_BUFFERS_1(0), // T1
		POWER_UP_DELAY_1(0), // power_up_delay
		POWER_DOWN_THRESHOLD_1(1), // power_down_threshold
		CONSUME_PENDING_TASK_TOKEN_1(2), // T5
		WAKE_UP_1(3), // T6
		START_SERVICE_1(4), // T2
		END_SERVICE_RATE_1(5), // service_rate


		// CPU 2
		START_BUFFERS_2(0), // T1
		POWER_UP_DELAY_2(0), // power_up_delay
		POWER_DOWN_THRESHOLD_2(1), // power_down_threshold
		CONSUME_PENDING_TASK_TOKEN_2(2), // T5
		WAKE_UP_2(3), // T6
		START_SERVICE_2(4), // T2
		END_SERVICE_RATE_2(5); // service_rate

		private final int transitionCode;

		Transitions(int transitionCode) {
			this.transitionCode = transitionCode;
		}


		public int getTransitionCode() {
			return transitionCode;
		}
	}

	PN() {
		double[] initialMarking = {0,0,8,0,0,10,15,0,0,5};
		mMarking = new Array2DRowRealMatrix(initialMarking);
		double[][] incidenceMatrix = {
				//Consumir en buffer 1,Consumir en buffer 2,Producir en buffer 1,Producir en buffer 2,T2,T3,T7,T8
				{-1, 0, 0, 0, 0, 1, 0, 0}, //Buffer 1
				{0, -1, 0, 0, 1, 0, 0, 0}, //Buffer 2
				{-1,-1, 0, 0, 0, 0, 1, 1}, //Consumidores
				{1,  0, 0, 0, 0, 0,-1, 0}, //Consumiendo buffer 1
				{0,  1, 0, 0, 0, 0, 0,-1}, //Consumiendo buffer 2
				{0,  0,-1, 0, 0, 0, 1, 0}, //Espacios buffer 1
				{0,  0, 0,-1, 0, 0, 0, 1}, //Espacios buffer 2
				{0,  0, 1, 0, 0,-1, 0, 0}, //Produciendo en buffer 1
				{0,  0, 0, 1,-1, 0, 0, 0}, //Produciendo en buffer 2
				{0,  0,-1,-1, 1, 1, 0, 0} //Productores
		};
		mIncidenceMatrix = new Array2DRowRealMatrix(incidenceMatrix);
	}

	void fire(Transitions transition) {
		mMarking =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
	}
	boolean isTransitionEnabled(Transitions transition) {
		Array2DRowRealMatrix matrix =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
		for (int i = 0; i<matrix.getRowDimension(); i++) {
			if (matrix.getRow(i)[0] < 0) return false;
		}
		return true;
	}

	List<Transitions> getEnabledTransitions() {
		List<Transitions> enabledTransitionsList = new ArrayList<>();
		for (Transitions t : Transitions.values()) {
			if (isTransitionEnabled(t)) enabledTransitionsList.add(t);
		}

		return enabledTransitionsList;
	}

	public String getMarkingString() {
		System.out.println(Arrays.toString(mMarking.transpose().getData()[0]));
		return Arrays.toString(mMarking.transpose().getData()[0]);
	}

	int getTotalTransitions() {
		return mIncidenceMatrix.getColumnDimension();
	}
}