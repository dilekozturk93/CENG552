package com.dataflowcoverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.GraphWalk;

/*
 * Dilek Öztürk
 */
public class DataFlowGraph {
	

	public static void main(String[] args) {
		String edgeInput = new String("1 2\n" + "2 3\n" + "3 4\n" + "3 7\n" + "4 5\n" + "4 6\n" + "5 6\n" + "6 3\n");
		// System.out.println("b");
		String defInput = new String("value 1 \n" + "b 1 2");
		String useInput = new String("value 3,7 3,4 4,5 5 4,6\n" + "b 3 4,6");
		Set<String> v = UserInputControl.vertexSet(edgeInput);
		Set<String> e = UserInputControl.edgeSet(edgeInput);

		Map<String, Set<String>> defs = UserInputControl.DUmap(defInput, v, e);
		Map<String, Set<String>> uses = UserInputControl.DUmap(useInput, v, e);

		Map<String, Set<String>> duPairs = DUpairs(defs, uses);

		Map<String, String> out = new HashMap<String, String>();

		for (String key : duPairs.keySet()) {
			Set<String> pairSet = duPairs.get(key);
			String outString = "";
			for (String pair : pairSet) {
				String[] pairArray = pair.split(" ");
				outString = outString + "[" + pairArray[0] + "," + pairArray[1] + "]" + "\n";
			}
			out.put(key, outString);
			outString = null;
		}
		System.out.println(out);
	}

	public static Graph<String, DefaultEdge> createGraph(Set<String> vertices, Set<String> edges) {

		Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

		for (String vertex : vertices) {
			g.addVertex(vertex);
		}

		for (String edge : edges) {
			String[] edgeArray = edge.split(",");

			g.addEdge(edgeArray[0], edgeArray[1]);
			edgeArray = null;
		}

		return g;
	}

	public static Set<String> allUseCoverage(Graph<String, DefaultEdge> graph, String initialNode, String finalNode,
			Set<String> varDefs, Set<String> varUses) {

		Set<String> allUseCoveragePaths = new HashSet<String>();
		Set<GraphPath<String, DefaultEdge>> usePaths = allUseCoverageReturnPath(graph, initialNode, finalNode, varDefs,
				varUses);

		for (GraphPath<String, DefaultEdge> path : usePaths) {
			allUseCoveragePaths.add(path.toString());
		}
		return allUseCoveragePaths;
	}

	public static List<GraphPath<String, DefaultEdge>> allUsePaths(Graph<String, DefaultEdge> graph,
			Set<String> varDefs, Set<String> varUses) {

		List<GraphPath<String, DefaultEdge>> allPaths = duSwitch(graph, varDefs, varUses);

		List<GraphPath<String, DefaultEdge>> allUsesPaths = new ArrayList<GraphPath<String, DefaultEdge>>();

		Set<String> cUses = new HashSet<String>();
		Set<String> pUses = new HashSet<String>();

		for (String use : varUses) {

			if (p_useORc_use(use) == 1) {
				pUses.add(use);
			} else {
				cUses.add(use);
			}
		}

		for (GraphPath<String, DefaultEdge> path : allPaths) {

			String endVertex = path.getEndVertex();

			String pathString = path.toString();
			String formatString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n]", "");
			String[] pathArray = formatString.split(" ");

			String endEdge = pathArray[pathArray.length - 2] + "," + pathArray[pathArray.length - 1];

			if (hasGlobalDefPath(path, varDefs, varUses) && ifDefClear(path, varDefs, varUses)
					&& cUses.contains(endVertex)) {
				allUsesPaths.add(path);
			}

			if (hasGlobalDefPath(path, varDefs, varUses) && ifDefClear(path, varDefs, varUses)
					&& pUses.contains(endEdge)) {
				allUsesPaths.add(path);
			}

		}
		return allUsesPaths;

	}

	public static Set<GraphPath<String, DefaultEdge>> allUseCoverageReturnPath(Graph<String, DefaultEdge> graph,
			String initialNode, String finalNode, Set<String> varDefs, Set<String> varUses) {

		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
		List<GraphPath<String, DefaultEdge>> allUsePaths = allUsePaths(graph, varDefs, varUses);

		Set<GraphPath<String, DefaultEdge>> allUseCoveragePaths = new HashSet<GraphPath<String, DefaultEdge>>();

		Set<String> initialSet = new HashSet<String>();
		initialSet.add(initialNode);
		Set<String> finalSet = new HashSet<String>();
		finalSet.add(finalNode);

		for (GraphPath<String, DefaultEdge> gp : allUsePaths) {
			List<String> vertexList = gp.getVertexList();
			Set<String> vertexSet = new HashSet<String>();

			for (String vertex : vertexList) {
				vertexSet.add(vertex);

				List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(initialSet, vertexSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, a.get(0).getVertexList(), 0);

				List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(vertexSet, finalSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, b.get(0).getVertexList(), 0);

				GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
						new Function<GraphWalk<String, DefaultEdge>, Double>() {

							@Override
							public Double apply(GraphWalk<String, DefaultEdge> t) {
								// TODO Auto-generated method stub
								return new Double(0.0);
							}
						});
				allUseCoveragePaths.add(abGW);

				vertexSet.remove(vertex);
				a = null;
				b = null;
				aGW = null;
				bGW = null;
			}

		}
		return allUseCoveragePaths;
	}

	public static Set<String> allDefCoverage(Graph<String, DefaultEdge> graph, String initialNode, String finalNode,
			Set<String> varDefs, Set<String> varUses) {

		Set<String> allDefCoveragePaths = new HashSet<String>();
		Set<GraphPath<String, DefaultEdge>> defPaths = allDefCoverageReturnPath(graph, initialNode, finalNode, varDefs,
				varUses);

		for (GraphPath<String, DefaultEdge> path : defPaths) {
			allDefCoveragePaths.add(path.toString());
		}
		return allDefCoveragePaths;
	}

	public static List<GraphPath<String, DefaultEdge>> allDefPaths(Graph<String, DefaultEdge> graph,
			Set<String> varDefs, Set<String> varUses) {

		List<GraphPath<String, DefaultEdge>> allPaths = duSwitch(graph, varDefs, varUses);

		List<GraphPath<String, DefaultEdge>> allDefpaths = new ArrayList<GraphPath<String, DefaultEdge>>();

		for (GraphPath<String, DefaultEdge> gp : allPaths) {
			if (hasGlobalDefPath(gp, varDefs, varUses) && ifDefClear(gp, varDefs, varUses)) {
				allDefpaths.add(gp);
			}
		}

		return allDefpaths;
	}

	public static Set<GraphPath<String, DefaultEdge>> allDefCoverageReturnPath(Graph<String, DefaultEdge> graph,
			String initialNode, String finalNode, Set<String> varDefs, Set<String> varUses) {

		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
		List<GraphPath<String, DefaultEdge>> allDefpaths = allDefPaths(graph, varDefs, varUses);

		Set<GraphPath<String, DefaultEdge>> allDefCoveragePaths = new HashSet<GraphPath<String, DefaultEdge>>();

		Set<String> initialSet = new HashSet<String>();
		initialSet.add(initialNode);
		Set<String> finalSet = new HashSet<String>();
		finalSet.add(finalNode);

		for (GraphPath<String, DefaultEdge> gp : allDefpaths) {
			List<String> vertexList = gp.getVertexList();
			Set<String> vertexSet = new HashSet<String>();

			for (String vertex : vertexList) {
				vertexSet.add(vertex);

				List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(initialSet, vertexSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, a.get(0).getVertexList(), 0);

				List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(vertexSet, finalSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, b.get(0).getVertexList(), 0);

				GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
						new Function<GraphWalk<String, DefaultEdge>, Double>() {

							@Override
							public Double apply(GraphWalk<String, DefaultEdge> t) {
								// TODO Auto-generated method stub
								return new Double(0.0);
							}
						});
				allDefCoveragePaths.add(abGW);

				vertexSet.remove(vertex);
				a = null;
				b = null;
				aGW = null;
				bGW = null;
			}

		}
		return allDefCoveragePaths;
	}

	public static Set<String> allDUpathCoverage(Graph<String, DefaultEdge> graph, String initialNode, String finalNode,
			Set<String> varDefs, Set<String> varUses) {

		Set<String> allDUcoveragePaths = new HashSet<String>();

		Set<GraphPath<String, DefaultEdge>> DUpaths = allDUpathCoverageReturnPath(graph,
				initialNode, finalNode, varDefs, varUses);

		for (GraphPath<String, DefaultEdge> path : DUpaths) {
			allDUcoveragePaths.add(path.toString());
		}
		return allDUcoveragePaths;
	}

	public static Set<GraphPath<String, DefaultEdge>> allDUpathCoverageReturnPath(Graph<String, DefaultEdge> graph,
			String initialNode, String finalNode, Set<String> varDefs, Set<String> varUses) {
		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);

		List<GraphPath<String, DefaultEdge>> DUpaths = DUpathsReturnPath(graph, varDefs, varUses);
		Set<GraphPath<String, DefaultEdge>> allDUcoveragePaths = new HashSet<GraphPath<String, DefaultEdge>>();

		Set<String> initialSet = new HashSet<String>();
		initialSet.add(initialNode);
		Set<String> finalSet = new HashSet<String>();
		finalSet.add(finalNode);

		for (GraphPath<String, DefaultEdge> gp : DUpaths) {
			List<String> vertexList = gp.getVertexList();
			Set<String> vertexSet = new HashSet<String>();

			for (String vertex : vertexList) {
				vertexSet.add(vertex);

				List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(initialSet, vertexSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, a.get(0).getVertexList(), 0);

				List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(vertexSet, finalSet, false,
						Integer.MAX_VALUE);
				GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, b.get(0).getVertexList(), 0);

				GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
						new Function<GraphWalk<String, DefaultEdge>, Double>() {

							@Override
							public Double apply(GraphWalk<String, DefaultEdge> t) {
								// TODO Auto-generated method stub
								return new Double(0.0);
							}
						});
				allDUcoveragePaths.add(abGW);

				vertexSet.remove(vertex);
				a = null;
				b = null;
				aGW = null;
				bGW = null;
			}

		}
		return allDUcoveragePaths;
	}
	
	public static Set<String> DUpaths(Graph<String, DefaultEdge> graph,Set<String> varDefs,Set<String> varUses){
		
		Set<String> DUpaths = new HashSet<String>();
		List<GraphPath<String, DefaultEdge>> DUpathsList = DUpathsReturnPath(graph, varDefs, varUses);
		
		for(GraphPath<String, DefaultEdge> path : DUpathsList) {
			DUpaths.add(path.toString());
		}
		
		return DUpaths;
	}

	public static List<GraphPath<String, DefaultEdge>> DUpathsReturnPath(Graph<String, DefaultEdge> graph, Set<String> varDefs,
			Set<String> varUses) {

		List<GraphPath<String, DefaultEdge>> allPaths = duSwitch(graph, varDefs, varUses);

		List<GraphPath<String, DefaultEdge>> DUpaths = new ArrayList<GraphPath<String, DefaultEdge>>();

		for (GraphPath<String, DefaultEdge> gp : allPaths) {

			if (hasGlobalDefPath(gp, varDefs, varUses) && ifDUpath(gp, varDefs, varUses)) {
				DUpaths.add(gp);
			}
		}
		return DUpaths;
	}

	public static Map<String, Set<String>> DUpairs(Map<String, Set<String>> defs, Map<String, Set<String>> uses) {

		Map<String, Set<String>> DUpairs = new HashMap<String, Set<String>>();

		Set<String> vars = varSet(defs, uses);
		Set<String> varDefs = defs.keySet();
		Set<String> varUses = uses.keySet();

		for (String var : vars) {
			if (varDefs.contains(var) && varUses.contains(var)) {
				Set<Set<String>> cartesianSet = cartesianProduct(defs.get(var), uses.get(var));

				Set<String> pairs = new HashSet<String>();

				for (Set<String> set : cartesianSet) {
					String temp = "";

					for (String s : set) {
						if (s.contains(","))
							temp = temp + "(" + s + ") ";
						else
							temp = temp + s + " ";

					}
					pairs.add(temp);
				}
				DUpairs.put(var, pairs);

			} else
				DUpairs.put(var, new HashSet<String>());
		}
		return DUpairs;
	}

	public static int DUtype(Set<String> varDefs, Set<String> varUses) {

		int defEdge = 0;
		int defNode = 0;
		int useEdge = 0;
		int useNode = 0;

		for (String d : varDefs) {
			if (d.contains(",")) {
				defEdge++;
			} else {
				defNode++;
			}
		}
		for (String u : varUses) {
			if (u.contains(",")) {
				useEdge++;
			} else {
				useNode++;
			}
		}

		if (defEdge == 0 && useEdge == 0) {
			return 1;
		} else if (defNode == 0 && useNode == 0) {
			return 2;
		} else
			return 3;
	}

	/*
	 * This method is to choose the correct method for definitions and uses user
	 * inputs
	 */
	public static List<GraphPath<String, DefaultEdge>> duSwitch(Graph<String, DefaultEdge> graph, Set<String> varDefs,
			Set<String> varUses) {

		List<GraphPath<String, DefaultEdge>> returnPath = new ArrayList<GraphPath<String, DefaultEdge>>();

		int status = DUtype(varDefs, varUses);

		switch (status) {

		case 1: {
			returnPath = duOnlyNode(graph, varDefs, varUses);
			return returnPath;
		}
		case 2: {
			returnPath = duOnlyEdge(graph, varDefs, varUses);
			return returnPath;
		}
		case 3: {
			returnPath = duNodeEdge(graph, varDefs, varUses);
			return returnPath;
		}
		default:
			return returnPath;
		}

	}

	/*
	 * This method is for definitions and uses user inputs which consist of only
	 * nodes
	 */
	public static List<GraphPath<String, DefaultEdge>> duOnlyNode(Graph<String, DefaultEdge> graph, Set<String> varDefs,
			Set<String> varUses) {

		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
		List<GraphPath<String, DefaultEdge>> returnPath = new ArrayList<GraphPath<String, DefaultEdge>>();

		List<GraphPath<String, DefaultEdge>> graphPath = directedPaths.getAllPaths(varDefs, varUses, false,
				Integer.MAX_VALUE);
		for (GraphPath<String, DefaultEdge> gp : graphPath) {
			List<String> vertexListPath = gp.getVertexList();
			GraphWalk<String, DefaultEdge> gw = new GraphWalk<>(graph, vertexListPath, 0);

			returnPath.add(gw);

		}

		return returnPath;

	}

	/*
	 * This method is for definitions and uses user inputs which consist of only
	 * edges
	 */
	public static List<GraphPath<String, DefaultEdge>> duOnlyEdge(Graph<String, DefaultEdge> graph, Set<String> varDefs,
			Set<String> varUses) {

		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
		List<GraphPath<String, DefaultEdge>> returnPath = new ArrayList<GraphPath<String, DefaultEdge>>();

		Set<String> setDEF0 = new HashSet<String>();
		Set<String> setDEF1 = new HashSet<String>();
		Set<String> setUSE0 = new HashSet<String>();
		Set<String> setUSE1 = new HashSet<String>();

		Set<Set<String>> cartPro = cartesianProduct(varDefs, varUses);

		for (Set<String> s : cartPro) {
			String setString = s.toString();
			// System.out.println("setString " + setString);

			String formatSetString = setString.replaceAll("[^a-zA-Z0-9\\s\\n,]", "");
			// System.out.println("formatSetString " + formatSetString);

			String[] e = formatSetString.split(", ");
			// System.out.println("e[0] + e[1] " + e[0] + " " + e[1]);
			String[] vDEF = e[0].split(",");

			setDEF0.add(vDEF[0]);
			setDEF1.add(vDEF[1]);
			// System.out.println("vDEF[0] +vDEFe[1] " + vDEF[0] + " " + vDEF[1]);
			String[] vUSE = e[1].split(",");

			setUSE0.add(vUSE[0]);
			setUSE1.add(vUSE[1]);
			// System.out.println("vUSE[0] +vUSE[1] " + vUSE[0] + " " + vUSE[1]);

			List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(setDEF0, setDEF1, false,
					Integer.MAX_VALUE);

			GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, a.get(0).getVertexList(), 0);
			// System.out.println("aGW " + aGW.toString());

			List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(setDEF1, setUSE0, false,
					Integer.MAX_VALUE);

			GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, b.get(0).getVertexList(), 0);
			// System.out.println("bGW " + bGW.toString());
			List<GraphPath<String, DefaultEdge>> c = directedPaths.getAllPaths(setUSE0, setUSE1, false,
					Integer.MAX_VALUE);
			GraphWalk<String, DefaultEdge> cGW = new GraphWalk<>(graph, c.get(0).getVertexList(), 0);
			// System.out.println("cGW " + cGW.toString());
			GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
					new Function<GraphWalk<String, DefaultEdge>, Double>() {

						@Override
						public Double apply(GraphWalk<String, DefaultEdge> t) {
							// TODO Auto-generated method stub
							return new Double(0.0);
						}
					});
			// System.out.println("abGW " + abGW.toString());

			GraphWalk<String, DefaultEdge> abcGW = abGW.concat(cGW,
					new Function<GraphWalk<String, DefaultEdge>, Double>() {

						@Override
						public Double apply(GraphWalk<String, DefaultEdge> t) {
							// TODO Auto-generated method stub
							return new Double(0.0);
						}
					});
			// System.out.println("abcGW " + abcGW.toString());

			returnPath.add(abcGW);
			setDEF0.remove(vDEF[0]);
			setDEF1.remove(vDEF[1]);
			setUSE0.remove(vUSE[0]);
			setUSE1.remove(vUSE[1]);
			e = null;
			vDEF = null;
			vUSE = null;
			setString = null;
			aGW = null;
			bGW = null;
			cGW = null;
			abGW = null;
			abcGW = null;
		}
		return returnPath;

	}

	/*
	 * This method is for definitions and uses user inputs which consist of both
	 * nodes and edges
	 */
	public static List<GraphPath<String, DefaultEdge>> duNodeEdge(Graph<String, DefaultEdge> graph, Set<String> varDefs,
			Set<String> varUses) {

		AllDirectedPaths<String, DefaultEdge> directedPaths = new AllDirectedPaths<>(graph);
		List<GraphPath<String, DefaultEdge>> returnPath = new ArrayList<GraphPath<String, DefaultEdge>>();

		Set<String> sourceVertices = new HashSet<String>();
		Set<String> targetVertices = new HashSet<String>();

		for (String def : varDefs) {

			if (!def.contains(",")) {
				sourceVertices.add(def);
			}
		}

		for (String use : varUses) {

			if (!use.contains(",")) {
				targetVertices.add(use);
			}
		}

		returnPath = duOnlyNode(graph, sourceVertices, targetVertices);

		for (String def : varDefs) {

			if (def.contains(",")) {
				String[] edge = def.split(",");

				Set<String> edgeSet0 = new HashSet<String>();
				edgeSet0.add(edge[0]);
				Set<String> edgeSet1 = new HashSet<String>();
				edgeSet1.add(edge[1]);

				List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(edgeSet0, edgeSet1, false,
						Integer.MAX_VALUE);

				List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(edgeSet1, targetVertices, false,
						Integer.MAX_VALUE);

				for (GraphPath<String, DefaultEdge> p : b) {

					GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, a.get(0).getVertexList(), 0);

					GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, p.getVertexList(), 0);

					GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
							new Function<GraphWalk<String, DefaultEdge>, Double>() {

								@Override
								public Double apply(GraphWalk<String, DefaultEdge> t) {
									// TODO Auto-generated method stub
									return new Double(0.0);
								}
							});

					returnPath.add(abGW);
				}
				edge = null;
			}
		}

		for (String use : varUses) {

			if (use.contains(",")) {
				String[] edge = use.split(",");
				Set<String> edgeSet0 = new HashSet<String>();
				edgeSet0.add(edge[0]);
				Set<String> edgeSet1 = new HashSet<String>();
				edgeSet1.add(edge[1]);

				List<GraphPath<String, DefaultEdge>> a = directedPaths.getAllPaths(sourceVertices, edgeSet0, false,
						Integer.MAX_VALUE);

				List<GraphPath<String, DefaultEdge>> b = directedPaths.getAllPaths(edgeSet0, edgeSet1, false,
						Integer.MAX_VALUE);

				Iterator<GraphPath<String, DefaultEdge>> it1 = a.iterator();

				while (it1.hasNext()) {

					GraphWalk<String, DefaultEdge> aGW = new GraphWalk<>(graph, it1.next().getVertexList(), 0);
					GraphWalk<String, DefaultEdge> bGW = new GraphWalk<>(graph, b.get(0).getVertexList(), 0);

					GraphWalk<String, DefaultEdge> abGW = aGW.concat(bGW,
							new Function<GraphWalk<String, DefaultEdge>, Double>() {

								@Override
								public Double apply(GraphWalk<String, DefaultEdge> t) {
									// TODO Auto-generated method stub
									return new Double(0.0);
								}
							});

					returnPath.add(abGW);
				}
				edge = null;
			}
		}

		return returnPath;
	}

	public static int p_useORc_use(String varUse) {

		if (varUse.contains(","))
			return 1; // p-use
		else
			return 2; // c-use
	}

	private static Set<String> varSet(Map<String, Set<String>> defs, Map<String, Set<String>> uses) {

		Set<String> varDefs = defs.keySet();
		Set<String> varUses = uses.keySet();
		Set<String> vars = new HashSet<String>();

		for (String s : varDefs) {
			vars.add(s);
		}

		for (String s : varUses) {
			vars.add(s);
		}

		return vars;

	}

	private static boolean hasGlobalDefPath(GraphPath<String, DefaultEdge> path, Set<String> varDefs,
			Set<String> varUses) {

		Set<String> cUses = new HashSet<String>();
		Set<String> pUses = new HashSet<String>();

		for (String use : varUses) {

			if (p_useORc_use(use) == 1) {
				pUses.add(use);
			} else {
				cUses.add(use);
			}
		}
		String startVertex = path.getStartVertex();
		String endVertex = path.getEndVertex();

		String pathString = path.toString();
		String formatString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n]", "");
		String[] pathArray = formatString.split(" ");

		String endEdge = pathArray[pathArray.length - 2] + "," + pathArray[pathArray.length - 1];

		boolean cUse = varDefs.contains(startVertex) && cUses.contains(endVertex) && ifDefClear(path, varDefs, varUses);
		boolean pUse = varDefs.contains(startVertex) && pUses.contains(endEdge) && ifDefClear(path, varDefs, varUses);

		return cUse || pUse;

	}

	private static boolean ifDUpath(GraphPath<String, DefaultEdge> path, Set<String> varDefs, Set<String> varUses) {

		Set<String> cUses = new HashSet<String>();
		Set<String> pUses = new HashSet<String>();

		for (String use : varUses) {

			if (p_useORc_use(use) == 1) {
				pUses.add(use);
			} else {
				cUses.add(use);
			}
		}
		String startVertex = path.getStartVertex();
		String endVertex = path.getEndVertex();

		String pathString = path.toString();
		String formatString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n]", "");
		String[] pathArray = formatString.split(" ");

		String endEdge = pathArray[pathArray.length - 2] + "," + pathArray[pathArray.length - 1];

		boolean cUse = varDefs.contains(startVertex) && cUses.contains(endVertex) && ifSimple(path)
				&& ifDefClear(path, varDefs, varUses);
		boolean pUse = varDefs.contains(startVertex) && pUses.contains(endEdge) && ifLoopFree(path)
				&& ifDefClear(path, varDefs, varUses);

		return cUse ^ pUse;
	}

	private static boolean ifSimple(GraphPath<String, DefaultEdge> path) {

		String pathString = path.toString();
		String formatString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n]", "");
		String[] pathArray = formatString.split(" ");
		Set<String> pathSet = new HashSet<String>();

		String subPath = "";

		for (int i = 1; i < pathArray.length - 1; i++) {
			subPath = subPath + pathArray[i];
			pathSet.add(pathArray[i]);
		}
		if (subPath.length() == pathSet.size())
			return true;
		else
			return false;
	}

	private static boolean ifLoopFree(GraphPath<String, DefaultEdge> path) {

		String pathString = path.toString();
		String formatString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n]", "");
		String[] pathArray = formatString.split(" ");
		Set<String> pathSet = new HashSet<String>();

		String subPath = "";

		for (int i = 0; i <= pathArray.length - 1; i++) {
			subPath = subPath + pathArray[i];
			pathSet.add(pathArray[i]);
		}
		if (subPath.length() == pathSet.size())
			return true;
		else
			return false;
	}

	private static boolean ifDefClear(GraphPath<String, DefaultEdge> path, Set<String> varDefs, Set<String> varUses) {

		String startVertex = path.getStartVertex();

		String pathString = path.toString();
		String formatSetString = pathString.replaceAll("[^a-zA-Z0-9\\s\\n,]", "");

		boolean defClear = true;

		if (varDefs.contains(startVertex)) {

			String[] pathArray = formatSetString.split(", ");
			String controlPath = "";
			/*
			 * String endControl = pathArray[pathArray.length - 2] + "," +
			 * pathArray[pathArray.length - 1]; System.out.println("endControl " +
			 * endControl); System.out.println(varUses.contains(endControl));
			 */

			for (int i = 1; i < pathArray.length - 1; i++) {
				controlPath = controlPath + pathArray[i] + " ";
			}

			if (controlPath.equals("")) {
				return true;
			}

			for (String def : varDefs) {
				defClear = !controlPath.contains(def) && defClear;
			}

			return defClear;

		}
		return false;
	}
	
	public static Set<String> getVars(Map<String,Set<String>> defs, Map<String,Set<String>> uses) {
		Set<String> vars = new HashSet<String>();
		
		defs.keySet().addAll(uses.keySet());
		vars.addAll(defs.keySet());
		
		return vars;
	}

	/*
	 * Method is copied from
	 * https://stackoverflow.com/questions/714108/cartesian-product-of-arbitrary-
	 * sets-in-java and its parameters changed
	 */
	@SafeVarargs
	private static Set<Set<String>> cartesianProduct(Set<String>... sets) {
		if (sets.length < 2)
			throw new IllegalArgumentException("Can't have a product of fewer than two sets (got " + sets.length + ")");

		return _cartesianProduct(0, sets);
	}

	private static Set<Set<String>> _cartesianProduct(int index, Set<String>... sets) {
		Set<Set<String>> ret = new HashSet<Set<String>>();

		if (index == sets.length) {
			ret.add(new HashSet<String>());
		} else {
			for (String obj : sets[index]) {
				for (Set<String> set : _cartesianProduct(index + 1, sets)) {
					set.add(obj);
					ret.add(set);
				}
			}
		}
		return ret;
	}


}
