var socket = new WebSocket('ws://localhost:8080/events')
var server_state = {}

socket.onopen = function() {
	socket.send('client-info`' + navigator.sayswho)
	socket.send('get-state')
}

socket.onclose = function(event) {
}

socket.onmessage = function(event) {
	var parts = event.data.split('`')
	switch (parts[0]) {
		case 'state-changed':
			socket.send('get-state')
			break
		case 'state':
			server_state = JSON.parse(parts[1])
			reflect_server_state()
			break
	}
}

socket.onerror = function(error) {
}

function reflect_sources(container_id, filter) {
	document.getElementById(container_id).innerHTML = server_state.sources.
		filter(filter).
		map(function(source) {
			var html = ['<div class=source-name>', source.name, '</div>']
			html.push('<div class=source-path>', source.path, '</div>')
			if (source.status) {
				html.push(
					'<div class=source-status-' + source.status_class + '>',
					source.status,
					'&nbsp;<span class=source-status-time>at ', source.status_time, '</span>',
					'</div>')
				if (source.status_details) {
					html.push('<pre class=source-status-details>', source.status_details, '</pre>')
				}
			}
			return html.join('')
		}).join('')
}

function reflect_server_state() {
	reflect_sources('repositories', function(source) { return !source.is_module})
	reflect_sources('modules', function(source) { return source.is_module})

	document.getElementById('connections').innerHTML = server_state.connections.
		map(function(connection) {
			return '<div class=source-name>' + connection.client_info +'</div>'
		}).join('')
}

navigator.sayswho= (function(){
    var ua= navigator.userAgent, tem,
    M= ua.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];
    if(/trident/i.test(M[1])){
        tem=  /\brv[ :]+(\d+)/g.exec(ua) || [];
        return 'IE '+(tem[1] || '');
    }
    if(M[1]=== 'Chrome'){
        tem= ua.match(/\b(OPR|Edge)\/(\d+)/);
        if(tem!= null) return tem.slice(1).join(' ').replace('OPR', 'Opera');
    }
    M= M[2]? [M[1], M[2]]: [navigator.appName, navigator.appVersion, '-?'];
    if((tem= ua.match(/version\/(\d+)/i))!= null) M.splice(1, 1, tem[1]);
    return M.join(' ');
})();
