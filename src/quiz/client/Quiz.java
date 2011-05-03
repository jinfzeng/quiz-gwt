package quiz.client;

import quiz.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.visualizations.BarChart;
import com.google.gwt.visualization.client.visualizations.BarChart.Options;
import java.util.ArrayList;

public class Quiz implements EntryPoint, ClickHandler {
	
	VerticalPanel mainPanel = new VerticalPanel();
	Button viewButton = new Button("Show Chart");
	String resp = "";
	String nameValue = new String();
	String pointValue = new String();
	int nodesLength = 0;
	int getNameIndex = 0;
	ArrayList<String> nameList = new ArrayList();
	ArrayList<Integer> pointList = new ArrayList();
	public void onModuleLoad() {
		viewButton.addClickHandler(this);
		mainPanel.add(viewButton);
		RootPanel.get().add(mainPanel);
	}
	private void getRequest(String url)
	{
		final RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,url);
		try {
			rb.sendRequest(null, new RequestCallback()
			{
				public void onError(final Request request, final Throwable exception)
				{
					Window.alert(exception.getMessage());
				}
				public void onResponseReceived(final Request request, final Response response)
				{
					String resp = response.getText();
					parseXML(resp);
					Runnable onLoadCallback = new Runnable()
					{
						public void run()
						{
							Panel panel = RootPanel.get();
							BarChart chart = new BarChart(createTable(),createOptions());
							panel.clear();
							panel.add(viewButton);
							panel.add(chart);
						}
					};
					VisualizationUtils.loadVisualizationApi(onLoadCallback, BarChart.PACKAGE);
				}
			});
		}
		catch (final Exception e) {
			Window.alert(e.getMessage());
		}
	}
	private Options createOptions()
	{
		Options options = Options.create();
		options.setWidth(1000);
		options.setHeight(340);
		options.setTitle("All Scores");
		return options;
	}
	private AbstractDataTable createTable()
	{
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING,"Name");
		data.addColumn(ColumnType.NUMBER,"point");
		data.addRows(nodesLength);
		for (int j = 0;j<nodesLength;j++) {
			data.setValue(j,0,nameList.get(j));
			data.setValue(j,1,pointList.get(j));
		}
		return data;
	}
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == viewButton) {
			String url = "http://localhost:3000/scores.xml";
			getRequest(url);
		}
	}
	private void parseXML(String xml)
	{
		String temp = xml;
		Document scoreDom = XMLParser.parse(temp);
	    Element scoreElement = scoreDom.getDocumentElement();
	    XMLParser.removeWhitespace(scoreElement);
	    NodeList nodes = scoreDom.getElementsByTagName("score");
	    nodesLength = nodes.getLength();
	    for (int i=0;i<nodesLength;i++) {
	    	nameValue = scoreElement.getElementsByTagName("username").item(i).getFirstChild().getNodeValue();
		    nameList.add(nameValue);
		    pointValue = scoreElement.getElementsByTagName("point").item(i).getFirstChild().getNodeValue();
		    pointList.add(Integer.parseInt(pointValue));
	    }
	}
}
