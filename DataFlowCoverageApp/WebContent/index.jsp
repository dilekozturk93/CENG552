<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Data Flow Graph Coverage</title>
</head>
<body bgcolor="#798EF6">
	<p align="center">
		<b><font color="#091034" size="6">Data Flow Graph Coverage
				Web Application</font></b>
	</p>
	<form name="dataFlowCoverageForm" action="ControllerServlet"
		method="post">
		<div style="text-align: center; font-weight: bold; font-size: 125%">
			<font color="#183BF0" size=5> Graph Information</font>
		</div>
		<table width="100%" cellspacing="0" cellpadding="0" bgcolor="#798EF6"
			border="3" bordercolor="#091034">
			<tbody>
				<tr>
					<td width="33%">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>Please
												enter your <b><font color="#183BF0">graph edges</font></b>
												in the text box below. Put each edge in one line. Enter
												edges as pairs of nodes, separated by spaces.(e.g.: 1 3)
										</b></font></td>
								</tr>
								<tr align="center">
									<td><p>
											<textarea name="edges" cols="30" rows="5"></textarea>
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>

					<td width="33%" valign="top">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>Enter <b><font
													color="#183BF0">initial nodes</font></b> below (can be more
												than one). If the text box below is empty, the first node in
												the left box will be the initial node.
										</b></font></td>
								</tr>
								<tr align="center">
									<td><p>
											<textarea name="initialNodes" cols="30" rows="5"></textarea>
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>

					<td width="34%" valign="top">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>Enter <b><font
													color="#183BF0">final nodes</font></b> below (can be more than
												one), separated by spaces.
												<td></td></b></font></td>
								</tr>
								<tr align="center">
									<td><p>
											<textarea name="finalNodes" cols="30" rows="5"></textarea>
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>

				</tr>
			</tbody>
		</table>

		<div style="text-align: center; font-weight: bold; font-size: 125%">
			<font color="#183BF0" size=5>Data Flow Information</font>
		</div>
		<table cellspacing="0" cellpadding="0" width="100%" bgcolor="#798EF6"
			border="3" bordercolor="#091034">
			<tbody>
				<tr>
					<td width="50%">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>Please
												enter your <b><font color="#183BF0">defs</font></b> in the
												text box below. Put one variable and all defs for the
												variable in one line, separated by spaces. Put the variable
												name at the beginning of the line.(e.g.: x 1 2)
										</b></font></td>
								</tr>
								<tr align="center">
									<td><p>
											<textarea name="definitons" cols="30" rows="5"></textarea>
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>

					<td width="50%" valign="top">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>Please
												enter your <b><font color="#183BF0">uses</font></b> in the
												text box below. Put one variable and all uses for the
												variable in one line, separated by spaces. Put the variable
												name at the beginning of the line. (e.g.: x 3 4 2,3)
										</b></font></td>
								</tr>
								<tr align="center">
									<td><p>
											<textarea name="uses" cols="30" rows="5"></textarea>
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>

				</tr>
			</tbody>
		</table>
		<div style="text-align: center; font-weight: bold; font-size: 125%">
			<font color="#183BF0" size=5>Coverage</font>
		</div>
		<table cellspacing="0" cellpadding="0" width="100%" bgcolor="#798EF6"
			border="3" bordercolor="#091034">
			<tbody>
				<tr>
					<td width="50%" valign="top">
						<table border="0">
							<tbody>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td align="right" width="15%"><font color="#091034" size=3><b>
												Requirements:</b></font></td>
									<td width="85%"><input type="submit" name="action"
										value="DU Pairs"> &nbsp; <input type="submit"
										name="action" value="DU Paths"></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td align="right" width="15%"><font color="#091034" size=3><b>
												Paths:</b></font></td>
									<td width="85%"><input type="submit"
										value="All Def Coverage" name="action"> &nbsp; <input
										type="submit" value="All Use Coverage" name="action">
										&nbsp; <input type="submit" value="All DU Path Coverage"
										name="action"></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td align="right" width="15%" d=""><font color="#091034"
										size=3><b>Others:</b> </font></td>
									<td width="85%"><input type="submit" value="New Graph"
										name="action">&nbsp;</td>
								</tr>
								<tr>
									<td></td>
								</tr>
								<tr>
									<td></td>
								</tr>
							</tbody>

						</table>
					</td>

					<td width="50%" valign="top">
						<table border="0">
							<tbody>
								<tr>
									<td><font color="#0A2299" size=4><b>You can
												upload your <b><font color="#183BF0">test file</font></b>
												and then download it.(This has not been developed yet.)
										</b></font></td>
								</tr>
								<tr align="left">
									<td><p>
											<font color="#091034" size=3><b>Test File:</b></font> 
											<input type="submit" name="action" value="File">
										</p></td>
								</tr>
							</tbody>
						</table>
					</td>



				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>


