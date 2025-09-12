package view;

import java.util.ArrayList;
import java.util.List;

public class HtmlTable {
	private List<String> headers = new ArrayList<>();
	private List<List<String>> rows = new ArrayList<>();
	
	private String escapeHtml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}

	public void setHeaders(String... headers) {
		this.headers.clear();
		for (String h : headers) {
			this.headers.add(h);
		}
	}

	public void addRow(String... cells) {
		List<String> row = new ArrayList<>();
		for (String c : cells) {
			row.add(c);
		}
		rows.add(row);
	}

	public String render() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class='table'>");
		sb.append("<thead><tr>");
		for (String h : headers) {
			sb.append("<th>").append(escapeHtml(h)).append("</th>");
		}
		sb.append("</tr></thead><tbody>");
		for (List<String> row : rows) {
			sb.append("<tr>");
			for (String cell : row) {
				sb.append("<td>").append(escapeHtml(cell)).append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</tbody></table>");
		return sb.toString();
	}
}