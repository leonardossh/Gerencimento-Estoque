package view;

public class HtmlForm {
	private String method;
	private String action;
	private StringBuilder fields = new StringBuilder();

	public HtmlForm(String method, String action) {
		this.method = escapeHtml(method);
		this.action = escapeHtml(action);
	}

	private String escapeHtml(String input) {
		if (input == null) return "";
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}

	public void addInput(String label, String name, String type) {
		addInput(label, name, type, true);
	}

	public void addInput(String label, String name, String type, boolean required) {
		fields.append("<div class='form-group'>");
		fields.append("<label>").append(escapeHtml(label)).append(":</label>");
		fields.append("<input type='").append(escapeHtml(type)).append("' name='").append(escapeHtml(name)).append("'");
		if (required) {
			fields.append(" required");
		}
		fields.append(">");
		fields.append("</div>");
	}

	public void addInput(String label, String name, String type, String value, boolean required) {
		fields.append("<div class='form-group'>");
		fields.append("<label>").append(escapeHtml(label)).append(":</label>");
		fields.append("<input type='").append(escapeHtml(type)).append("' name='").append(escapeHtml(name)).append("'");

		if (value != null && !value.isEmpty()) {
			fields.append(" value='").append(escapeHtml(value)).append("'");
		}

		if (required) {
			fields.append(" required");
		}

		fields.append(">");
		fields.append("</div>");
	}

	public void addButton(String label) {
		fields.append("<div class='form-group'>");
		fields.append("<input type='submit' value='").append(escapeHtml(label)).append("' class='btn'>");
		fields.append("</div>");
	}

	public void addRawHtml(String html) {
		fields.append(html);
	}

	public void enableDebug() {
		fields.append("<div style='background:#eef;padding:10px;border:1px solid #ccc;margin-top:10px'>");
		fields.append("<strong>DEBUG:</strong> Verifique se todos os campos possuem o atributo <code>name</code>.<br>");
		fields.append("</div>");
	}

	public String render() {
		StringBuilder result = new StringBuilder();
		result.append("<div class='container'><div class='card'><form method='").append(method).append("' action='").append(action).append("'>")
			  .append(fields.toString())
			  .append("</form></div></div>");
		return result.toString();
	}
}
