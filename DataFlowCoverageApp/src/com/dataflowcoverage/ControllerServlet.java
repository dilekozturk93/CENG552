package com.dataflowcoverage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jgrapht.*;
import org.jgrapht.graph.*;
/*
 * Dilek Öztürk
 */
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String edges;
	private String initialNode;
	private String finalNode;
	private String defs;
	private String uses;

	private Set<String> edgeSetInput;
	private Set<String> vertexSetInput;
	private Map<String, Set<String>> defMapInput;
	private Map<String, Set<String>> useMapInput;
	private Set<String> vars;

	private Graph<String, DefaultEdge> graph;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");

		String action = request.getParameter("action");

		if (action.equals("DU Pairs")) {
			getParameters(request);

			if (checkCode() != 0 && checkCode() != -1) {
				errorPageDecision(request, response, idCalculator(checkCode()));
			} else if (checkCode() == 0) {
				createInputs();

				graph = DataFlowGraph.createGraph(vertexSetInput, edgeSetInput);

				Map<String, Set<String>> duPairs = DataFlowGraph.DUpairs(defMapInput, useMapInput);
				Map<String, String> out = new HashMap<String, String>();

				for (String key : duPairs.keySet()) {
					Set<String> pairSet = duPairs.get(key);
					String outString = "";
					for (String pair : pairSet) {
						String[] pairArray = pair.split(" ");
						outString = outString + "[" + pairArray[0] + "," + pairArray[1] + "]" + "\n";
					}
					out.put(key + "  ", outString);
					System.out.println("outString " + outString);
					outString = null;
				}
				request.setAttribute("DUpairs", out);

				RequestDispatcher rd = request.getRequestDispatcher("DUpairs.jsp");
				rd.forward(request, response);

			}

		} else if (action.equals("DU Paths")) {
			getParameters(request);
			if (checkCode() != 0 && checkCode() != -1) {
				errorPageDecision(request, response, idCalculator(checkCode()));
			} else if (checkCode() == 0) {
				createInputs();
				graph = DataFlowGraph.createGraph(vertexSetInput, edgeSetInput);
				vars = DataFlowGraph.getVars(defMapInput, useMapInput);

				Map<String, String> out = new HashMap<String, String>();

				for (String variable : vars) {

					Set<String> du = DataFlowGraph.DUpaths(graph, defMapInput.get(variable), useMapInput.get(variable));
					String outString = "";

					for (String s : du) {
						outString = outString + s + "\n";
					}
					out.put(variable, outString);
				}

				request.setAttribute("DUpaths", out);

				RequestDispatcher rd = request.getRequestDispatcher("DUpaths.jsp");
				rd.forward(request, response);
			}

		} else if (action.equals("All Def Coverage")) {
			getParameters(request);
			if (checkCode() != 0 && checkCode() != -1) {
				errorPageDecision(request, response, idCalculator(checkCode()));
			} else if (checkCode() == 0) {
				createInputs();
				graph = DataFlowGraph.createGraph(vertexSetInput, edgeSetInput);
				vars = DataFlowGraph.getVars(defMapInput, useMapInput);

				Map<String, String> out = new HashMap<String, String>();

				for (String variable : vars) {

					Set<String> du = DataFlowGraph.allDefCoverage(graph, initialNode, finalNode,
							defMapInput.get(variable), useMapInput.get(variable));
					String outString = "";

					for (String s : du) {
						outString = outString + s + "\n";
					}
					out.put(variable, outString);
				}

				request.setAttribute("AllDef", out);

				RequestDispatcher rd = request.getRequestDispatcher("AllDefCoverage.jsp");
				rd.forward(request, response);
			}

		} else if (action.equals("All Use Coverage")) {
			getParameters(request);
			if (checkCode() != 0 && checkCode() != -1) {
				errorPageDecision(request, response, idCalculator(checkCode()));
			} else if (checkCode() == 0) {
				createInputs();
				graph = DataFlowGraph.createGraph(vertexSetInput, edgeSetInput);
				vars = DataFlowGraph.getVars(defMapInput, useMapInput);

				Map<String, String> out = new HashMap<String, String>();

				for (String variable : vars) {

					Set<String> du = DataFlowGraph.allUseCoverage(graph, initialNode, finalNode,
							defMapInput.get(variable), useMapInput.get(variable));
					String outString = "";

					for (String s : du) {
						outString = outString + s + "\n";
					}
					out.put(variable, outString);
				}

				request.setAttribute("AllUse", out);

				RequestDispatcher rd = request.getRequestDispatcher("AllUseCoverage.jsp");
				rd.forward(request, response);
			}
		} else if (action.equals("All DU Path Coverage")) {
			getParameters(request);
			if (checkCode() != 0 && checkCode() != -1) {
				errorPageDecision(request, response, idCalculator(checkCode()));
			}else if(checkCode() == 0) {
				createInputs();
				graph = DataFlowGraph.createGraph(vertexSetInput, edgeSetInput);
				vars = DataFlowGraph.getVars(defMapInput, useMapInput);
				
				Map<String,String> out = new HashMap<String,String>();
				
				for(String variable : vars) {
					
					Set<String> du = DataFlowGraph.allDUpathCoverage(graph, initialNode,finalNode
							,defMapInput.get(variable), useMapInput.get(variable));
					String outString = "";
					
					for(String s : du) {
						outString = outString + s + "\n";
					}
					out.put(variable, outString);
				}
				
				 request.setAttribute("AllDU",out);
				 
				RequestDispatcher rd = request.getRequestDispatcher("AllDUCoverage.jsp");
				rd.forward(request, response);
			}

		} else if (action.equals("New Graph")) {

		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private void getParameters(HttpServletRequest request) {

		edges = request.getParameter("edges");
		initialNode = request.getParameter("initialNodes");
		finalNode = request.getParameter("finalNodes");
		defs = request.getParameter("definitons");
		uses = request.getParameter("uses");
	}

	private void createInputs() {

		edgeSetInput = UserInputControl.edgeSet(edges);
		vertexSetInput = UserInputControl.vertexSet(edges);
		defMapInput = UserInputControl.DUmap(defs, vertexSetInput, edgeSetInput);
		useMapInput = UserInputControl.DUmap(uses, vertexSetInput, edgeSetInput);
	}

	private void errorPageDecision(HttpServletRequest request, HttpServletResponse response, int id)
			throws ServletException, IOException {

		switch (id) {

		case 10: {
			RequestDispatcher rd = request.getRequestDispatcher("invalidEdgeInput.jsp");
			rd.forward(request, response);
		}
		case 20: {
			RequestDispatcher rd = request.getRequestDispatcher("invalidInitialNodeInput.jsp");
			rd.forward(request, response);
		}
		case 30: {
			RequestDispatcher rd = request.getRequestDispatcher("invalidFinalNodeInput.jsp");
			rd.forward(request, response);
		}
		case 40: {
			RequestDispatcher rd = request.getRequestDispatcher("invalidDefInput.jsp");
			rd.forward(request, response);
		}
		case 50: {
			RequestDispatcher rd = request.getRequestDispatcher("invalidUseInput.jsp");
			rd.forward(request, response);
		}
		}
	}

	private int checkCode() {

		if (checkEdgeInput() == 0 && checkVertices() == 0 && checkInitialNodeInput() == 0 && checkFinalNodeInput() == 0
				&& checkDefInput() == 0 && checkUsesInput() == 0)
			return 0;
		else if (checkEdgeInput() != 0)
			return checkEdgeInput();

		else if (checkInitialNodeInput() != 0)
			return checkInitialNodeInput();

		else if (checkFinalNodeInput() != 0)
			return checkFinalNodeInput();

		else if (checkDefInput() != 0)
			return checkDefInput();

		else if (checkUsesInput() != 0)
			return checkUsesInput();

		else if (checkVertices() != 0)
			return checkVertices();

		return -1;
	}

	private int idCalculator(int checkCode) {

		int residual = checkCode % 10;

		return checkCode - residual;

	}

	private int checkEdgeInput() {

		int id = 10;
		if (!UserInputControl.inputControl(edges)) {
			return 1 + id;
		}
		if (!UserInputControl.edgeInputControl(edges)) {
			return 2 + id;
		}
		if (UserInputControl.edgeSet(edges).isEmpty()) {
			return 3 + id;
		}
		return 0;
	}

	private int checkVertices() {
		int id = 10;

		if (UserInputControl.vertexSet(edges).isEmpty()) {
			return 1 + id;
		}
		return 0;
	}

	private int checkInitialNodeInput() {
		int id = 20;

		if (!UserInputControl.inputControl(initialNode)) {
			return 1 + id;
		}
		if (UserInputControl.initialOrFinalNode(initialNode).equals("")) {
			return 2 + id;
		}

		return 0;
	}

	private int checkFinalNodeInput() {
		int id = 30;

		if (!UserInputControl.inputControl(finalNode)) {
			return 1 + id;
		}
		if (UserInputControl.initialOrFinalNode(finalNode).equals("")) {
			return 2 + id;
		}

		return 0;
	}

	private int checkDefInput() {
		int id = 40;

		if (!UserInputControl.inputControl(defs)) {
			return 1 + id;
		}
		if (UserInputControl.DUmap(defs, UserInputControl.vertexSet(edges), UserInputControl.edgeSet(edges))
				.isEmpty()) {
			return 2 + id;
		}
		return 0;
	}

	private int checkUsesInput() {
		int id = 50;
		if (!UserInputControl.inputControl(uses)) {
			return 1 + id;
		}
		if (UserInputControl.DUmap(uses, UserInputControl.vertexSet(edges), UserInputControl.edgeSet(edges))
				.isEmpty()) {
			return 2 + id;
		}
		return 0;
	}

}