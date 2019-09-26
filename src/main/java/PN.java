import org.apache.commons.math3.linear.Array2DRowRealMatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PN {
	private final Array2DRowRealMatrix mIncidenceMatrix;
	private final Array2DRowRealMatrix mForwardsIncidenceMatrix;
	private final Array2DRowRealMatrix mBackwardsIncidenceMatrix;
	private final Array2DRowRealMatrix mInhibitionMatrix;

	private Array2DRowRealMatrix mMarking;

	private final PInvariants[] invariants;
	private final boolean checkInvariants;
	private boolean verbose = false;

	public enum Transitions {
		ARRIVAL_RATE(Main.ARRIVAL_RATE_1_ALFA, Main.ARRIVAL_RATE_1_BETA),
		CONSUME_PENDING_TASK_TOKEN_1,
		CONSUME_PENDING_TASK_TOKEN_2,
		END_SERVICE_RATE_1(Main.SERVICE_RATE_1_ALFA, Main.SERVICE_RATE_1_BETA),
		END_SERVICE_RATE_2(Main.SERVICE_RATE_2_ALFA, Main.SERVICE_RATE_2_BETA),
		POWER_DOWN_THRESHOLD_1,
		POWER_DOWN_THRESHOLD_2,
		POWER_UP_DELAY_1,
		POWER_UP_DELAY_2,
		START_BUFFER_1,
		START_BUFFER_2,
		START_SERVICE_1,
		START_SERVICE_2,
		WAKE_UP_1,
		WAKE_UP_2;

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
		C1_IDLE,
		C2_IDLE,
		CORE1_BUFFER,
		CORE1_ACTIVE,
		CORE2_BUFFER,
		CORE2_ACTIVE,
		CPU1_ON,
		CPU1_POWER_UP,
		CPU1_STAND_BY,
		CPU2_ON,
		CPU2_POWER_UP,
		CPU2_STAND_BY,
		P0,
		P1,
		P6,
		P8;


		public int getPosition() {
			return ordinal();
		}
	}

	class PInvariants {
		final int invariant;

		final Places[] places;

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
		double[] initialMarking = {1,1,0,0,0,0,0,0,1,0,0,1,1,0,0,0};
		mMarking = new Array2DRowRealMatrix(initialMarking);

		this.invariants = new PInvariants[]{
				new PInvariants(1, Places.P0, Places.P1),
				new PInvariants(1, Places.C1_IDLE, Places.CORE1_ACTIVE),
				new PInvariants(1, Places.C2_IDLE, Places.CORE2_ACTIVE),
				new PInvariants(1, Places.CPU1_ON, Places.CPU1_POWER_UP, Places.CPU1_STAND_BY),
				new PInvariants(1, Places.CPU2_ON, Places.CPU2_POWER_UP, Places.CPU2_STAND_BY),
		};
		this.checkInvariants = checkInvariants;

		double[][] forwardsIncidenceMatrix = {
			{0,0,0,1,0,0,0,0,0,0,0,0,0,0,0}, // C1_IDLE
			{0,0,0,0,1,0,0,0,0,0,0,0,0,0,0}, // C2_IDLE
			{0,0,0,0,0,0,0,0,0,1,0,0,0,0,0}, // CORE1_BUFFER
			{0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}, // CORE1_ACTIVE
			{0,0,0,0,0,0,0,0,0,0,1,0,0,0,0}, // CORE2_BUFFER
			{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0}, // CORE2_ACTIVE
			{0,1,0,0,0,0,0,1,0,0,0,1,0,0,0}, // CPU1_ON
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}, // CPU1_POWER_UP
			{0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, // CPU1_STAND_BY
			{0,0,1,0,0,0,0,0,1,0,0,0,1,0,0}, // CPU2_ON
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // CPU2_POWER_UP
			{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0}, // CPU2_STAND_BY
			{0,0,0,0,0,0,0,0,0,1,1,0,0,0,0}, // P0
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P1
			{0,0,0,0,0,0,0,0,0,1,0,0,0,1,0}, // P6
			{0,0,0,0,0,0,0,0,0,0,1,0,0,0,1}  // P8
		};
		double[][] backwardsIncidenceMatrix = {
			{0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}, // C1_IDLE
			{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0}, // C2_IDLE
			{0,0,0,0,0,0,0,0,0,0,0,1,0,0,0}, // CORE1_BUFFER
			{0,0,0,1,0,0,0,0,0,0,0,0,0,0,0}, // CORE1_ACTIVE
			{0,0,0,0,0,0,0,0,0,0,0,0,1,0,0}, // CORE2_BUFFER
			{0,0,0,0,1,0,0,0,0,0,0,0,0,0,0}, // CORE2_ACTIVE
			{0,1,0,0,0,1,0,0,0,0,0,1,0,0,0}, // CPU1_ON
			{0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}, // CPU1_POWER_UP
			{0,0,0,0,0,0,0,0,0,0,0,0,0,1,0}, // CPU1_STAND_BY
			{0,0,1,0,0,0,1,0,0,0,0,0,1,0,0}, // CPU2_ON
			{0,0,0,0,0,0,0,0,1,0,0,0,0,0,0}, // CPU2_POWER_UP
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,1}, // CPU2_STAND_BY
			{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P0
			{0,0,0,0,0,0,0,0,0,1,1,0,0,0,0}, // P1
			{0,1,0,0,0,0,0,1,0,0,0,0,0,1,0}, // P6
			{0,0,1,0,0,0,0,0,1,0,0,0,0,0,1}  // P8
		};
		double[][] inhibitionMatrix = {
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c1-Idle
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // c2-Idle
			{0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, // core1_buffer
			{0,0,0,0,0,1,0,0,0,0,0,0,0,0,0}, // core1-active
			{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0}, // core2_buffer
			{0,0,0,0,0,0,1,0,0,0,0,0,0,0,0}, // core2-active
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1_ON
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1-Power_up
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU1-Stand_by
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2_ON
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2-Power_up
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // CPU2-Stand_by
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P0
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P1
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, // P6
			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}  // P8
		};

		mForwardsIncidenceMatrix = new Array2DRowRealMatrix(forwardsIncidenceMatrix);
		mBackwardsIncidenceMatrix = new Array2DRowRealMatrix(backwardsIncidenceMatrix);

		mIncidenceMatrix = mForwardsIncidenceMatrix.subtract(mBackwardsIncidenceMatrix);
		mInhibitionMatrix = new Array2DRowRealMatrix(inhibitionMatrix);
		initInternalCounters();
		System.out.println("Comenzando RdP...");
	}

	void fire(Transitions transition) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
		if (verbose) System.out.println("Marcado antes: " + getMarkingString() + sdf.format(new Date()) + ", " + transition.toString());
		mMarking =
				mMarking.add(new Array2DRowRealMatrix(mIncidenceMatrix.getColumn(transition.getTransitionCode())));
		if(transition.isTemporized()) transition.setInitialTime(null);
		initInternalCounters();
		assert checkPInvariant();
		//if (verbose) System.out.println("Marcado desp.: " + getMarkingString() + sdf.format(new Date()) + ", " + transition.toString());
		//if (verbose) System.out.println("-------------------------------");
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
	/**
	 * @return 0 if transition is not enabled
	 * 		   1 if transition is enabled and not temporized
	 * 		   the (negative) remaining millis for timed transition to be enabled*/
	int isTransitionEnabled(Transitions transition) {
		Array2DRowRealMatrix matrix =
				mMarking.subtract(new Array2DRowRealMatrix(mBackwardsIncidenceMatrix.getColumn(transition.getTransitionCode())));
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
				mMarking.subtract(new Array2DRowRealMatrix(mBackwardsIncidenceMatrix.getColumn(transition.getTransitionCode())));
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
