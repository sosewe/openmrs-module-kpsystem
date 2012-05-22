<%
	ui.decorateWith("standardPage")
	def APPS_PER_ROW = 3;
%>

<style>
	.app-button {
		border: 1px black solid;
		background-color: #e0e0e0;
		padding: 0.3em;
		margin: 0.5em;
		float: left;
		text-align: center;
		width: 10em;
		height: 7em;
	}
		
	#homepage-logo {
		float: left;
		margin-right: 1em;
	}
	
	#homepage-motd {
		float: left;
		font-size: 1.2em;
		font-weight: bold;
		padding-top: 1em;
	}
	
	#homepage-apps {
		clear: both;
		text-align: center;
		padding-top: 0.5em;
	}
</style>

<img id="homepage-logo" src="${ ui.resourceLink("kenyaemr", "images/logo.png") }"/>

<div id="homepage-motd">
	${ motd }
</div>

<div id="homepage-apps">	
	<% apps.eachWithIndex { app, i -> %>
		<div class="app-button" <% if (i % APPS_PER_ROW == 0) { %> style="clear: left;" <% } %>>
			<a href="/${ contextPath }/${ app.homepageUrl }">
				<% if (app.iconUrl) { %>
					<img class="app-icon" src="/${ contextPath }/${ app.iconUrl }" height="64"/><br/>
				<% } %>
				<span class="app-label">
					${ app.label }
				</span>
			</a>
		</div>
	<% } %>
</div>