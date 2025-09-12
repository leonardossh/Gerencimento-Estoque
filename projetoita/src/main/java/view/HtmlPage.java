package view;

public class HtmlPage {

	private String title;
	private String body;
	private String contextPath;

	public HtmlPage(String title) {
		this.title = title;
		this.body = "";
		this.contextPath = "";
	}

	public HtmlPage(String title, String contextPath) {
		this.title = title;
		this.body = "";
		this.contextPath = contextPath;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void addToBody(String content) {
		this.body += content;
	}

	public void addBackButton() {
		this.body = "<a href='javascript:history.back()' class='back-btn'>Voltar</a>" + this.body;
	}

	public void addBackButton(String url) {
		this.body = "<a href='" + url + "' class='back-btn'>Voltar</a>" + this.body;
	}

	public String render() {
		String backButton = "";
		if (!title.equals("Login")) {
			backButton = "<a href='javascript:history.back()' class='back-btn-red'>Voltar</a>";
		}
		
		return "<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"<head>\n" + 
			"    <meta charset='UTF-8'>\n" +
			"    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" + 
			"    <title>" + title + "</title>\n" +
			"    <style>\n" +
			"        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
			"        body { font-family: Arial, sans-serif; background: #f0f2f5; min-height: 100vh; }\n" +
			"        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
			"        .card { background: white; border-radius: 10px; padding: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-top: 20px; }\n" +
			"        h1, h2 { text-align: center; color: #333; margin-bottom: 20px; }\n" +
			"        .form-group { margin-bottom: 20px; }\n" +
			"        label { display: block; margin-bottom: 5px; color: #555; font-weight: bold; }\n" +
			"        input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; }\n" +
			"        input:focus, select:focus, textarea:focus { outline: none; border-color: #007bff; }\n" +
			"        .btn { display: block; width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }\n" +
			"        .btn:hover { background: #0056b3; }\n" +
			"        .menu-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }\n" +
			"        .menu-item { background: #007bff; padding: 20px; text-align: center; border-radius: 8px; text-decoration: none; color: white; font-weight: bold; }\n" +
			"        .menu-item:hover { background: #0056b3; color: white; text-decoration: none; }\n" +
			"        .success-msg { background: #28a745; color: white; padding: 15px; border-radius: 5px; text-align: center; margin-bottom: 20px; }\n" +
			"        .error-msg { background: #dc3545; color: white; padding: 15px; border-radius: 5px; text-align: center; margin-bottom: 20px; }\n" +
			"        .table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n" +
			"        .table th, .table td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }\n" +
			"        .table th { background: #f8f9fa; font-weight: bold; }\n" +
			"        .back-btn-red { position: fixed; top: 20px; right: 20px; background: #dc3545; color: white; padding: 10px 15px; border-radius: 5px; text-decoration: none; font-weight: bold; }\n" +
			"        .back-btn-red:hover { background: #c82333; color: white; text-decoration: none; }\n" +
			"        @media (max-width: 768px) { .menu-grid { grid-template-columns: 1fr; } }\n" +
			"    </style>\n" +
			"</head>\n" +
			"<body>\n" + 
			body + "\n" + 
			backButton + "\n" + 
			"</body>\n" + 
			"</html>";
	}
}