import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class PN {
	private Array2DRowRealMatrix mIncidenceMatrix;
	private Array2DRowRealMatrix mInhibitionMatrix;

	private Array2DRowRealMatrix mMarking;

	private PInvariants[] invariants;
	private boolean checkInvariants;

//	Arrival_rate
//			c1_Service_start
//	c1-Service_rate
//			c2_Service_start
//	c2-Service_rate
//	CPU1-ConsumePendingTask
//	CPU1-Power_down_threshold
//	CPU1-Power_up_delay
//	CPU1-ReturnPendingTask
//	CPU1-StartBuffer
//	CPU1-WakeUp
//			CPU2_power_up_delay
//	CPU2-ConsumePendingTask
//	CPU2-Power_down_threshold
//	CPU2-ReturnPendingTask
//	CPU2-StartBuffer
//	CPU2-WakeUp
//			T19
//	T20
//			ZT17
//	ZT18
	// TODO: poner bien los transition codes
	enum Transitions {
		ARRIVAL_RATE(Main.ARRIVAL_RATE_1_ALFA, Main.ARRIVAL_RATE_1_BETA), // arrival_rate h
		START_SERVICE_1(), // T2 h
		END_SERVICE_RATE_1(Main.SERVICE_RATE_1_ALFA, Main.SERVICE_RATE_1_BETA), // service_rate h
		START_SERVICE_2(), // T2 h
		END_SERVICE_RATE_2(Main.SERVICE_RATE_2_ALFA, Main.SERVICE_RATE_2_BETA), // service_rate h
		CONSUME_PENDING_TASK_TOKEN_1(), // T5 h
		POWER_DOWN_THRESHOLD_1(), // power_down_threshold h
		POWER_UP_DELAY_1(), // power_up_delay h
        RETURN_PENDING_TASK_1(),
		START_BUFFER_1(), // T1 h
		WAKE_UP_1(), // T6 h
		POWER_UP_DELAY_2(), // power_up_delay h
		CONSUME_PENDING_TASK_TOKEN_2(), // T5 h
		POWER_DOWN_THRESHOLD_2(), // power_down_threshold h
		RETURN_PENDING_TASK_2(),
		START_BUFFER_2(), // T1 h
		WAKE_UP_2(), // T6 h
		ZT19(), // ZT17 h
		ZT20,
		ZT17(), // ZT17 h
		ZT18(); // ZT17 h

		private Integer alfa;
		private Integer beta;
		private Date initialTime;

		Transitions(){}

		/**
		 * @param alfa  millis
		 * @param beta  millis
		 */
		Transitions(Integer alfa, Integer beta) {
			this.alfa = alfa;
			this.beta = beta;
		}

		public int getTransitionCode() {
			return this.ordinal();
		}

		public boolean isTemporized() {
			return (alfa != null && beta != null);
		}

		public Date getInitialTime() {
			return initialTime;
		}

		public void setInitialTime(Date initialTime) {
			this.initialTime = initialTime;
		}
	}
	enum Places {
		c1_idle(0),
		c2_idle(1),
		Buffer1(2),
		core1_active(3),
		Buffer2(4),
		core2_active(5),
		CPU1_ON(6),
		CPU1_PowerUp(7),
		CPU1_StandBy(8),
		CPU2_ON(9),
		CPU2_PowerUp(10),
		CPU2_StandBy(11),
		P0(12),
		P1(13),
		P20(16),
		P21(17),

		Z18(20),
		ZP19(21);

		int position;
		Places (int position) {
			this.position = position;
		}

		public int getPosition() {
			return position;
		}
	}

	class PInvariants {
		final int invariant;

		Places[] places;

		PInvariants(int invariant, Places... places){
			this.invariant = invariant;
			this.places = places;
		}

		boolean checkInvariant(Array2DRowRealMatrix marking) {
			double value = 0;

			for (Places p: places) {
				value += marking.getEntry(p.getPosition(), 0);
			}

			return (value == invariant);
		}
	}

	PN(boolean checkInvariants) {
		double[] initialMarking = {1,1,0,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0};
		mMarking = new Array2DRowRealMatrix(initialMarking);

		this.invariants = new PInvariants[]{
				new PInvariants(1, Places.P0, Places.P1),
				new PInvariants(1, Places.c1_idle, Places.core1_active),
				new PInvariants(1, Places.c2_idle, Places.core2_active),
				new PInvariants(1, Places.CPU1_ON, Places.CPU1_PowerUp, Places.CPU1_StandBy, Places.Z18, Places.P20),
				new PInvariants(1, Places.CPU2_ON, Places.CPU2_PowerUp, Places.CPU2_StandBy, Places.ZP19, Places.P21),
		};
		this.checkInvariants = checkInvariants;

		double[][] incidenceMatrix = {
				{0,-1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c1-Idle
				{0,0,0,-1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c2-Idle
				{0,-1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0}, // core1_buffer
				{0,1,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // core1-active
				{0,0,0,-1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0}, // core2_buffer
				{0,0,0,1,-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // core2-active
				{0,-1,0,0,0,-1,-1,1,0,0,0,0,0,0,0,0,0,1,0,1,0}, // CPU1_ON
				{0,0,0,0,0,0,0,-1,0,0,1,0,0,0,0,0,0,0,0,0,0}, // CPU1-Power_up
				{0,0,0,0,0,0,1,0,0,0,-1,0,0,0,0,0,0,0,0,0,0}, // CPU1-Stand_by
				{0,0,0,-1,0,0,0,0,0,0,0,1,-1,-1,0,0,0,0,1,0,1}, // CPU2_ON
				{0,0,0,0,0,0,0,0,0,0,0,-1,0,0,0,0,1,0,0,0,0}, // CPU2-Power_up
				{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,-1,0,0,0,0}, // CPU2-Stand_by
				{-1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0}, // P0
				{1,0,0,0,0,0,0,0,0,-1,0,0,0,0,0,-1,0,0,0,0,0}, // P1
				{0,0,0,0,0,0,0,0,-1,0,1,0,0,0,0,0,0,0,0,0,0}, // P16
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0,1,0,0,0,0}, // P17
				{0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0,0,0}, // P20
				{0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0,0}, // P21
				{0,0,0,0,0,-1,0,-1,1,1,-1,0,0,0,0,0,0,0,0,0,0}, // P6
				{0,0,0,0,0,0,0,0,0,0,0,-1,-1,0,1,1,-1,0,0,0,0}, // P8
				{0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,-1,0}, // Z18
				{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,-1}, // ZP19
		};
		double[][] inhibitionMatrix = {
				// Arrival_rate, c1-Service_rate, c2-Service_rate, CPU1-Power_down_threshold, CPU1-Power_up_delay, CPU2_power_up_delay, CPU2-Power_down_threshold, T1, T2, T3, T4, T5, CPU1-WakeUp, T7, T8
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c1-Idle
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c2-Idle
				{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // core1_buffer
				{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // core1-active
				{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}, // core2_buffer
				{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}, // core2-active
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1_ON
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1-Power_up
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1-Stand_by
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2_ON
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2-Power_up
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2-Stand_by
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P0
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P1
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P16
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P17
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P20
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P21
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P6
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P8
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // Z18
				{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // ZP19
		};
		mIncidenceMatrix = new Array2DRowRealMatrix(incidenceMatrix);
		mInhibitionMatrix = new Array2DRowRealMatrix(inhibitionMatrix);
		initInternalCounters();
	}

	void fire(Transitions transition) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		// System.out.println("Marcado antes: " + getMarkingString() + sdf.format(new Date()) + ", " + transition.toString());
		mMarking =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
		if(transition.isTemporized()) transition.setInitialTime(null);
		initInternalCounters();
		assert checkPInvariant();
	}

	private boolean checkPInvariant() {
		for (PInvariants pi: invariants)
			if (!pi.checkInvariant(mMarking)) return false;

		return true;
	}

	private void initInternalCounters() {
		for (Transitions t : getEnabledTransitionsWithoutTime()) {
			// Si es una transición temporizada y no se inició su contador interno
			if (t.isTemporized() && t.getInitialTime() == null)  {
				t.setInitialTime(new Date());
			}
		}
	}

	// WithTime
	int isTransitionEnabled(Transitions transition) {
		Array2DRowRealMatrix matrix =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
		for (int i = 0; i<matrix.getRowDimension(); i++) {
			if (matrix.getRow(i)[0] < 0)  {
				return 0;
			}
		}

		// Inhibition
		double inhibition =
				mMarking.transpose().multiply(new Array2DRowRealMatrix(
						mInhibitionMatrix.getColumn(transition.getTransitionCode())))
							.getRow(0)[0];
		if (inhibition != 0) return 0;

		// Timed transitions
		if (transition.isTemporized() && transition.getInitialTime() != null) {
			long elapsedTimeMilis = new Date().getTime() - transition.getInitialTime().getTime();
			if (elapsedTimeMilis >= transition.alfa){
			    if(elapsedTimeMilis <= transition.beta) {
					return 1;
				}
			    else{
			    	System.out.println(transition + " Se pasó del tiempo beta.");
			    	transition.setInitialTime(null);
			    	return 0;
				}
			}else{
				return (int) (elapsedTimeMilis - transition.alfa);
			}
		}
		return 1;
	}

	private boolean isTransitionEnabledWithoutTime(Transitions transition) {
		Array2DRowRealMatrix matrix =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
		for (int i = 0; i<matrix.getRowDimension(); i++) {
			if (matrix.getRow(i)[0] < 0) return false;
		}
		// Inhibition
		double inhibition =
				mMarking.transpose().multiply(new Array2DRowRealMatrix(
						mInhibitionMatrix.getColumn(transition.getTransitionCode())))
						.getRow(0)[0];
		if (inhibition != 0) return false;
		return true;
	}

	// withTime
	List<Transitions> getEnabledTransitions() {
		List<Transitions> enabledTransitionsList = new ArrayList<>();
		for (Transitions t : Transitions.values()) {
			if (isTransitionEnabled(t)==1) enabledTransitionsList.add(t);
		}
		return enabledTransitionsList;
	}
	private List<Transitions> getEnabledTransitionsWithoutTime() {
		List<Transitions> enabledTransitionsList = new ArrayList<>();
		for (Transitions t : Transitions.values()) {
			if (isTransitionEnabledWithoutTime(t)) enabledTransitionsList.add(t);
		}

		return enabledTransitionsList;
	}

	public int getPlaceTokens(Places p) {
		return (int) mMarking.getRow(p.getPosition())[0];
	}

	public String getMarkingString() {
		//System.out.println(Arrays.toString(mMarking.transpose().getData()[0]));
		return Arrays.toString(mMarking.transpose().getData()[0]);
	}

	int getTotalTransitions() {
		return mIncidenceMatrix.getColumnDimension();
	}
}