/*TODO recorded refactoring
 * обработка ошибок перенесена в вызывающий код
 * *********************
 * в метод public void Run(Collection<AbstractNode> nodes, OperationalNode debugNode)
 *  добавлен параметр public AbstractProcessingProgressActions processingActions - это ссылка на объект, выполняющий GUI действия перед стартом
 * *********************
 */

/*
 _______________________
 Apatar Open Source Data Integration
 Copyright (C) 2005-2007, Apatar, Inc.
 info@apatar.com
 195 Meadow St., 2nd Floor
 Chicopee, MA 01013

 ### This program is free software; you can redistribute it and/or modify
 ### it under the terms of the GNU General Public License as published by
 ### the Free Software Foundation; either version 2 of the License, or
 ### (at your option) any later version.

 ### This program is distributed in the hope that it will be useful,
 ### but WITHOUT ANY WARRANTY; without even the implied warranty of
 ### MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.# See the
 ### GNU General Public License for more details.

 ### You should have received a copy of the GNU General Public License along
 ### with this program; if not, write to the Free Software Foundation, Inc.,
 ### 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ________________________

 */

package com.apatar.core;

import java.net.SocketException;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;

public class Runnable {
	// whether to perform method TransformRDBtoTDB of the node if there no nodes
	// connected after it.
	private boolean readLastNodeData = false;

	public Runnable(boolean readLastNodeData) {
		super();
		this.readLastNodeData = readLastNodeData;
	}

	public Runnable() {
		super();
	}

	// this is wrapper to move processing to another thread
	public class Run extends Thread {
		Collection<AbstractNode> nodes;
		OperationalNode debugNode = null;
		Runnable rn = null;
		private CountDownLatch doneSignal = null;

		public Run(Collection<AbstractNode> nodes, OperationalNode debugNode,
				Runnable rn) {
			this.nodes = nodes;
			this.debugNode = debugNode;
			this.rn = rn;
		}

		public Run(Collection<AbstractNode> nodes, OperationalNode debugNode,
				Runnable rn, CountDownLatch doneSignal) {
			this.nodes = nodes;
			this.debugNode = debugNode;
			this.rn = rn;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {
			GregorianCalendar startDate = new GregorianCalendar();

			System.out.println("******************* Start Time = "
					+ startDate.getTime().toString());
			if (debugNode != null) {
				try {
					rn.executeToNode(debugNode);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			} else {
				try {
					rn.execute(nodes);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}

			GregorianCalendar finishDate = new GregorianCalendar();

			System.out.println("******************* Finish Time = "
					+ finishDate.getTime().toString());
			System.out.println("************* Total time = "
					+ (finishDate.getTimeInMillis() - startDate
							.getTimeInMillis()));

			Runtime.getRuntime().gc();
			// this is finish of the processing;
			ApplicationData.ProcessingProgress.Finish();
			if (doneSignal != null) {
				doneSignal.countDown();
			}
		}

	}

	private CountDownLatch doneSignal = null;

	public void Run(Collection<AbstractNode> nodes, OperationalNode debugNode,
			AbstractProcessingProgressActions processingActions,
			CountDownLatch doneSignal) {
		this.doneSignal = doneSignal;
		Run(nodes, debugNode, processingActions);
	}

	public void Run(Collection<AbstractNode> nodes, OperationalNode debugNode,
			AbstractProcessingProgressActions processingActions) {
		if (ApplicationData.withoutUI) {
			ApplicationData.ProcessingProgress = new IProcessingProgress() {
				public void Finish() {
				}

				public Boolean Log(String message) {
					return null;
				}

				public boolean Log(Exception e) {
					return false;
				}

				public void NodeEnter() {
				}

				public void Reset() {
				}

				public boolean Status() {
					return false;
				}

				public Boolean Step() {
					return true;
				}
			};
		} else {
			if (null != processingActions) {
				processingActions.beforeStart(debugNode);
			}
		}

		Thread th;
		if (doneSignal != null) {
			th = new Run(nodes, debugNode, this, doneSignal);
		} else {
			th = new Run(nodes, debugNode, this);
		}
		// ((Run) th).setReadLastNodeData(readLastNodeData);

		th.start();
	}

	// execute to the specified node
	private void executeToNode(AbstractNode debugNode) throws SocketException {
		Collection<AbstractNode> executeList = CoreUtils
				.getChainNodes(debugNode);
		execute(executeList);
	}

	public void execute(Collection<AbstractNode> nodes) throws SocketException {

		CoreUtils.definitionExecutionOrder(nodes);
		ITransformer[] nodeArray = CoreUtils.Sort(nodes);
		execute(nodeArray);
	}

	private void execute(ITransformer[] nodes) throws SocketException {

		for (ITransformer element : nodes) {
			execute(element);
		}
	}

	private void execute(ITransformer node) throws SocketException {
		if (node instanceof OperationalNode) {
			ApplicationData.ProcessingProgress.Log(node.getClass().getName()
					+ " created.");
		}

		node.BeforeExecute();
		// call the NodeEnter only for Operational Nodes
		if (node instanceof OperationalNode) {
			ApplicationData.ProcessingProgress.NodeEnter();
		}

		// execute processing and catch main exceptions
		// string error exception
		// String error = null;
		if (node instanceof AbstractDataBaseNode) {
			((AbstractDataBaseNode) node).Transform(readLastNodeData);
		} else {
			node.Transform();
		}

		// if (error != null) {
		// JOptionPane.showMessageDialog(null, error);
		// System.out.println(error);
		// }
	}

}
