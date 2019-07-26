package renyi;

import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.Map.Entry;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws Exception
    {
        if (args.length != 5) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String presetAnswerPath = args[3];
        String answerPath = args[4];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " presetAnswerPath = " + presetAnswerPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");


				List<Integer[]> roadList = readTxt(roadPath);

				List<Integer[]> crossList = readTxt(crossPath);

				List<Integer[]> carList = readTxt(carPath);


				List<Integer[]> presetAnswerList = readTxt(presetAnswerPath);
				Map<Object,Integer> map = new HashMap<Object, Integer>();

				SortLength sortLength = new SortLength();
				Collections.sort(roadList, sortLength);

				Integer maxLength = roadList.get(roadList.size() - 1)[1];
				Integer minLength = roadList.get(0)[1];

				SortChannel sortChannel = new SortChannel();
				Collections.sort(roadList, sortChannel);

				double maxChannel = roadList.get(roadList.size() - 1)[3];
				double minChannel = roadList.get(0)[3];

				SortRoadId sortRoadId = new SortRoadId();
				Collections.sort(roadList, sortRoadId);

				double[][] xArray = new double[roadList.size()][2];
				double[][] pArray = new double[roadList.size()][2];
				for (int i = 0; i < roadList.size(); i++) {
					xArray[i][0] = (maxLength - roadList.get(i)[1]) / (maxLength - minLength+0.01);
					xArray[i][1] = (roadList.get(i)[3] - minChannel) / (maxChannel - minChannel+0.01);
					if (xArray[i][0] == 0.0) {
						xArray[i][0] = 1;
					}
					if (xArray[i][1] == 0.0) {
						xArray[i][1] = 1;
					}
				}
				double xAllLength = 0.0;
				for (int i = 0; i < xArray.length; i++) {
					xAllLength += xArray[i][0];
				}
				double xAllChannel = 0.0;
				for (int i = 0; i < xArray.length; i++) {
					xAllChannel += xArray[i][1];
				}
				for (int i = 0; i < roadList.size(); i++) {
					pArray[i][0] = xArray[i][0] / xAllLength;
					pArray[i][1] = xArray[i][1] / xAllChannel;
				}
				double eLength = 0.0;
				for (int i = 0; i < roadList.size(); i++) {

							eLength += -1 / Math.log(roadList.size()) * pArray[i][0] * Math.log(pArray[i][0]);



				}
				double eChannel = 0.0;
				for (int i = 0; i < roadList.size(); i++) {

						eChannel += -1 / Math.log(roadList.size()) * pArray[i][1] * Math.log(pArray[i][1]);


				}
				double dLength = 1 - eLength;
				double dChannel = 1 - eChannel;
				double wLength = dLength / (dLength + dChannel);
				double wChannel = dChannel / (dLength + dChannel);

				double[] sRoad = new double[roadList.size()];
				for (int i = 0; i < sRoad.length; i++) {
					sRoad[i] = wLength * pArray[i][0] + wChannel * pArray[i][1];

				}

				Integer[] sRoadInt = new Integer[roadList.size()];
				for (int i = 0; i < sRoadInt.length; i++) {
					sRoadInt[i] = (int) Math.round(sRoad[i] * 10000);

					roadList.get(i)[1] = sRoadInt[i];

				}





				SortCarSpeedprior sort = new SortCarSpeedprior();
				Collections.sort(carList, sort);
                List<Integer[]> carListTime = readTxt(carPath);
				SortCarSpeedprior sort333 = new SortCarSpeedprior();
				Collections.sort(carListTime, sort333);


				int number = 0;
				int time = 1;
				int car = 45;
				int count = 0;
//				for(int i =0;i<carList.size();i++) {
//					System.out.println(carList.get(i)[0]);
//				}
                int maxTime = 0;
				for (int i = 0; i < presetAnswerList.size(); i++) {
					maxTime = Math.max(maxTime, presetAnswerList.get(i)[1]);
				}
				while(carListTime.size()!=number) {
					for(int i = 0;i<presetAnswerList.size();i++) {
						if(presetAnswerList.get(i)[1].equals(time)) {
							count++;
							number++;
						}
					}

					int uu = 12;
					if(time<maxTime) {
					if(uu > 0) {
						for(int i = 0;i<carListTime.size();i++) {
							if (uu == 0) {
								break;
							}
							if(carListTime.get(i) != null && carListTime.get(i)[6].equals(0) && carListTime.get(i)[4]<=time) {
								//System.out.println(carListTime.get(i)[0] + " " + carList.get(i)[0]);
								carList.get(i)[4] = time;
								System.out.println("time:" + time);
								carListTime.set(i, null);
								number++;
								uu--;
							}

						}
					}
					}
					else {
						int leftCar = car - count;
					if (leftCar > 0) {
						for(int i = 0;i<carListTime.size();i++) {
							if (leftCar == 0) {
								break;
							}
							if(carListTime.get(i) != null && carListTime.get(i)[6].equals(0) && carListTime.get(i)[4]<=time) {
								//System.out.println(carListTime.get(i)[0] + " " + carList.get(i)[0]);
								carList.get(i)[4] = time;
								System.out.println("time:" + time);
								carListTime.set(i, null);
								number++;
								leftCar--;
							}

						}
					}
					}
						time = time + 1;


					count = 0;
					//System.out.println(number);
				}





		HashMap<Integer, HashMap<Integer, Integer>> stepLength = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (int i = 0; i < crossList.size(); i++) {
					HashMap<Integer, Integer> step = new HashMap<Integer, Integer>();
						for (int j = 0; j < roadList.size(); j++) {
						if (roadList.get(j)[4].equals(crossList.get(i)[0])) {
							step.put(roadList.get(j)[5], roadList.get(j)[1]);
						}else if(roadList.get(j)[6] == 1 && roadList.get(j)[5].equals(crossList.get(i)[0])){
							step.put(roadList.get(j)[4], roadList.get(j)[1]);
						}
					}
			stepLength.put(crossList.get(i)[0], step);
		}
		FileWriter fWriter = new FileWriter(answerPath);
		fWriter.write("#(carId,StartTime,RoadId...)");
        fWriter.write("\n");

		  for(int i = 0;i<presetAnswerList.size();i++) {
				fWriter.write("("+presetAnswerList.get(i)[0]+","+presetAnswerList.get(i)[1]);
				fWriter.flush();

				for(int j = 2;j<presetAnswerList.get(i).length;j++) {
					fWriter.write(","+presetAnswerList.get(i)[j]);
					fWriter.flush();
					for(int k = 0;k<roadList.size();k++) {
						if(presetAnswerList.get(i)[j].equals(roadList.get(k)[0])&&roadList.get(k)[6].equals(1)) {
							if(k != roadList.size()-1) {
								int x = stepLength.get(roadList.get(k)[4]).get(roadList.get(k)[5]);
								stepLength.get(roadList.get(k)[4]).replace((Integer) (roadList.get(k)[5]), x+1);
								int y = stepLength.get(roadList.get(k)[5]).get(roadList.get(k)[4]);
								stepLength.get(roadList.get(k)[5]).replace((Integer) (roadList.get(k)[4]), y+1);
							}

						}else if(presetAnswerList.get(i)[j].equals(roadList.get(k)[0])&&roadList.get(k)[6].equals(0)) {

							int x = stepLength.get(roadList.get(k)[4]).get(roadList.get(k)[5]);
							stepLength.get(roadList.get(k)[4]).replace((Integer) (roadList.get(k)[5]), x+1);
						}
					}


				}
				fWriter.write(")");
				fWriter.flush();
				//if (i != presetAnswerList.size() - 1) {
					fWriter.write("\n");
					fWriter.flush();
				//}

			}

		for (int i = 0; i < carList.size(); i++) {
			if(carList.get(i)[6].equals(0)) {
				fWriter.write("(" + carList.get(i)[0] + "," + (carList.get(i)[4]) + ",");
				fWriter.flush();
				DistanceDijkstraImpl distance = new DistanceDijkstraImpl();
				List step111 = distance.getMinStep(carList.get(i)[1], carList.get(i)[2], stepLength).getStep();


				List list = turnRoad(step111, crossList, roadList);
				for (int j = 0; j < list.size(); j++) {
					if (j == (list.size() - 1)) {
						fWriter.write(list.get(j) + "");
						fWriter.flush();
						continue;
					}
					fWriter.write(list.get(j) + ",");
					if(j!=list.size()-1) {
								int x = stepLength.get(step111.get(j)).get(step111.get(j+1));
								stepLength.get(step111.get(j)).replace((Integer) (step111.get(j+1)), x+1);

							}

					fWriter.flush();
				}
				fWriter.write(")");
				fWriter.flush();

				if (i != carList.size() - 1) {
					fWriter.write("\n");
					fWriter.flush();
				}

			}

		}

        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }
    public static List<Integer> turnRoad(List<Integer> list, List<Integer[]> crossList, List<Integer[]> roadList) {
		List<Integer> list1 = new ArrayList<Integer>();
		HashMap<Integer, HashMap<Integer, Integer>> roadMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		for (int i = 0; i < crossList.size(); i++) {
			HashMap<Integer, Integer> step = new HashMap<Integer, Integer>();
			for (int j = 0; j < roadList.size(); j++) {
				if (roadList.get(j)[4].equals(crossList.get(i)[0])) {
					step.put(roadList.get(j)[5], roadList.get(j)[0]);
				} else if (roadList.get(j)[6] == 1 && roadList.get(j)[5].equals(crossList.get(i)[0])) {
					step.put(roadList.get(j)[4], roadList.get(j)[0]);
				}
			}
			roadMap.put(crossList.get(i)[0], step);
		}
		Iterator<Entry<Integer, HashMap<Integer, Integer>>> iterator = roadMap.entrySet().iterator();
		for (int i = 0; i < list.size() - 1; i++) {
			list1.add(roadMap.get(list.get(i)).get(list.get(i + 1)));
		}
		return list1;
	}

	public static List<Integer[]> readTxt(String str) throws Exception {
		File file = new File(str);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		line = bufferedReader.readLine();
		List<Integer[]> list1 = new ArrayList<Integer[]>();
		while (line != null) {
			String[] str1 = line.split(",");
			for(int i = 0;i<str1.length;i++) {
				if(Character.isSpace(str1[i].charAt(0))) {
					str1[i] = str1[i].substring(1);
				}
				//str1[i].trim();
				//System.out.println(str1[i]);
			}

			str1[0] = str1[0].substring(1, str1[0].length());
			str1[str1.length - 1] = str1[str1.length - 1].substring(0, str1[str1.length - 1].length() - 1);
			Integer[] str2 = new Integer[str1.length];
			for (int i = 0; i < str1.length; i++) {
				str2[i] = Integer.parseInt(str1[i]);

			}
			list1.add(str2);
			line = bufferedReader.readLine();
		}
		bufferedReader.close();
		fileReader.close();
		return list1;
	}

}
class DistanceDijkstraImpl {
	public static final MinStep UNREACHABLE = new MinStep(false, -1);
	private HashMap<Integer, HashMap<Integer, Integer>> stepLength;
	private int nodeNum;
	private HashSet<Integer> outNode;
	private HashMap<Integer, PreNode> nodeStep;
	private LinkedList<Integer> nextNode;
	private int startNode;
	private int endNode;

	public MinStep getMinStep(int start, int end, final HashMap<Integer, HashMap<Integer, Integer>> stepLength) {
		this.stepLength = stepLength;
		this.nodeNum = this.stepLength != null ? this.stepLength.size() : 0;
		if (this.stepLength == null || (!this.stepLength.containsKey(start)) || (!this.stepLength.containsKey(end))) {
			return UNREACHABLE;
		}
		initProperty(start, end);
		step();
		if (nodeStep.containsKey(end)) {
			return changeToMinStep();
		}
		return UNREACHABLE;
	}

	private MinStep changeToMinStep() {
		MinStep minStep = new MinStep();
		minStep.setMinStep(nodeStep.get(endNode).getNodeStep());
		minStep.setReachable(true);
		LinkedList<Integer> step = new LinkedList<Integer>();
		minStep.setStep(step);
		int nodeNum = endNode;
		step.addFirst(nodeNum);
		while (nodeStep.containsKey(nodeNum)) {
			int node = nodeStep.get(nodeNum).getPreNodeNum();
			step.addFirst(node);
			nodeNum = node;
		}
		return minStep;
	}

	private void initProperty(int start, int end) {
		outNode = new HashSet<Integer>();
		nodeStep = new HashMap<Integer, PreNode>();
		nextNode = new LinkedList<Integer>();
		nextNode.add(start);
		startNode = start;
		endNode = end;
	}

	private void step() {
		if (nextNode == null || nextNode.size() < 1) {
			return;
		}
		if (outNode.size() == nodeNum) {
			return;
		}
		int start = nextNode.removeFirst();
		int step = 0;
		if (nodeStep.containsKey(start)) {
			step = nodeStep.get(start).getNodeStep();
		}
		HashMap<Integer, Integer> nextStep = stepLength.get(start);
		Iterator<Entry<Integer, Integer>> iter = nextStep.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Integer> entry = iter.next();
			Integer key = entry.getKey();

			if (key == startNode) {
				continue;
			}

			Integer value = entry.getValue() + step;
			if ((!nextNode.contains(key)) && (!outNode.contains(key))) {
				nextNode.add(key);
			}
			if (nodeStep.containsKey(key)) {
				if (value < nodeStep.get(key).getNodeStep()) {
					nodeStep.put(key, new PreNode(start, value));
				}
			} else {
				nodeStep.put(key, new PreNode(start, value));
			}
		}
		outNode.add(start);
		step();
	}
}

class PreNode {
	private int preNodeNum;
	private int nodeStep;

	public PreNode(int preNodeNum, int nodeStep) {
		this.preNodeNum = preNodeNum;
		this.nodeStep = nodeStep;
	}

	public int getPreNodeNum() {
		return preNodeNum;
	}

	public void setPreNodeNum(int preNodeNum) {
		this.preNodeNum = preNodeNum;
	}

	public int getNodeStep() {
		return nodeStep;
	}

	public void setNodeStep(int nodeStep) {
		this.nodeStep = nodeStep;
	}
}

class MinStep {
	private boolean reachable;
	private int minStep;
	private List<Integer> step;

	public MinStep() {
	}

	public MinStep(boolean reachable, int minStep) {
		this.reachable = reachable;
		this.minStep = minStep;
	}

	public boolean isReachable() {
		return reachable;
	}

	public void setReachable(boolean reachable) {
		this.reachable = reachable;
	}

	public int getMinStep() {
		return minStep;
	}

	public void setMinStep(int minStep) {
		this.minStep = minStep;
	}

	public List<Integer> getStep() {
		return step;
	}

	public void setStep(List<Integer> step) {
		this.step = step;
	}
}
class CarInfo {
	private String carId;
	private List<String> carPath;
	public static String carFrom;
	public static String carTo;
	private String carSpeed;
	private String carPlanTime;
	private String carActTime;
	private String stateCross;
	private String nowRoad;
	private String nowRoadTurn;
	Boolean complete = false;


	public CarInfo(String carId, String carSpeed, String carPlanTime) {
		super();
		this.carId = carId;
		this.carSpeed = carSpeed;
		this.carPlanTime = carPlanTime;
	}


	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}


	public List<String> getCarPath() {
		return carPath;
	}

//	public void setCarPath() throws Exception {
//		List<String> path = SetPath.setPath();
//		this.carPath = path;
//	}


	public String getCarFrom() {
		return carFrom;
	}

	public void setCarFrom() {
		this.carFrom = this.carPath.get(0);
	}


	public String getCarTo() {
		return carTo;
	}

	public void setCarTo() {
		if (this.carPath.size() > 1) {
			this.carTo = this.carPath.get(1);
		} else {
			this.carTo = "end";
		}
	}


	public String getCarSpeed() {
		return carSpeed;
	}

	public void setCarSpeed(String carSpeed) {
		this.carSpeed = carSpeed;
	}


	public String getCarPlanTime() {
		return carPlanTime;
	}

	public void setCarPlanTime(String carPlanTime) {
		this.carPlanTime = carPlanTime;
	}


	public String getCarActTime() {
		return carActTime;
	}

	public void setCarActTime(String carActTime) {
		this.carActTime = carActTime;
	}


	public String getStateCross() {
		return stateCross;
	}

	public void setStateCross(String stateCross) {
		this.stateCross = stateCross;
	}


	public String getNowRoad() {
		return nowRoad;
	}
	public String getNowRoadTurn() {
		return nowRoadTurn;
	}
	// #(id,length,speed,channel,from,to,isDuplex)
	// (5000, 10, 5, 1, 1, 2, 1)
	public void setNowRoad(List<String[]> roadList) {
		for (int i = 0; i < roadList.size(); i++) {
			if (roadList.get(i)[4].equals(this.carFrom) && roadList.get(i)[5].equals(this.carTo)) {
				this.nowRoad = roadList.get(i)[0];
				this.nowRoadTurn = null;
			} else if (roadList.get(i)[5].equals(this.carFrom) && roadList.get(i)[4].equals(this.carTo)
					&& roadList.get(i)[6].equals("1")) {
				this.nowRoad = null;
				this.nowRoadTurn = roadList.get(i)[0];
			}

		}
	}

}


//#(id,length,speed,channel,from,to,isDuplex)
//(5000, 10, 5, 1, 1, 2, 1)

	 class SortLength implements Comparator<Integer[]>{
		@Override
		public int compare(Integer[] o1, Integer[] o2) {
			// TODO Auto-generated method stub
			if (o1[1] < o2[1]) {
				return -1;
			} else if (o1[1] > o2[1]) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	 class SortChannel implements Comparator<Integer[]>{
		@Override
		public int compare(Integer[] o1, Integer[] o2) {
			// TODO Auto-generated method stub
			if (o1[3] < o2[3]) {
				return -1;
			} else if (o1[3] > o2[3]) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	 class SortRoadId implements Comparator<Integer[]>{
		@Override
		public int compare(Integer[] o1, Integer[] o2) {
			// TODO Auto-generated method stub
			if (o1[0] < o2[0]) {
				return -1;
			} else if (o1[0] > o2[0]) {
				return 1;
			} else {
				return 0;
			}
		}

	}
		 class SortCarSpeedprior implements Comparator<Integer[]> {
			@Override
			public int compare(Integer[] o1, Integer[] o2) {
				// TODO Auto-generated method stub
				if (o1[5] < o2[5]) {
					return 1;
				} else if (o1[5] > o2[5]) {
					return -1;
				} else {
					if(o1[3]<o2[3]) {
						return 1;
					}else if(o1[3]>o2[3]) {
						return -1;
					}else {
						return 0;
					}
				}
			}

		}
		class SortCarPresetPlantime implements Comparator<Integer[]>{
		@Override
		public int compare(Integer[] o1, Integer[] o2) {
			// TODO Auto-generated method stub
			if (o1[6] < o2[6]) {
				return 1;
			} else if (o1[6] > o2[6]) {
				return -1;
			} else {
				if(o1[4]<o2[4]) {
					return 1;
				}else if(o1[4]>o2[4]) {
					return -1;
				}else {
					return 0;
				}
			}
		}

	}

