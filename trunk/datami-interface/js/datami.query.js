 

var siteFilters = new Array();
var entityFilters = new Array();

function siteFilterPatterns(){
    if (siteFilters.length==0) return "";     
    if (siteFilters.length==1){      
	return '?r <http://weblifelog.com/ontology/toSite> <'+siteFilters[0]+'>.';
    }
    var result="{";
    for (x in siteFilters){
	if (x!=0) result+=" UNION ";
	result+='{?r <http://weblifelog.com/ontology/toSite> <'+siteFilters[x]+'>}';
    }
    result += "}";
    return result;
}


function entityFilterPatterns(){
    result = "";
    for (x in entityFilters){
	result+='?r <http://datami.co.uk/ontology/relatedTo> ?tea'+x+'. ?tea'+x+' <http://fise.iks-project.eu/ontology/entity-reference> <'+entityFilters[x]+'>. ';
    }
    return result;
}


function dateFilterPatterns(){
    if (calendarPicker == null ) return "";
    startDate = calendarPicker.startDate;
    endDate = calendarPicker.endDate;
    if (startDate == null || endDate==null) return "";
    sdm = startDate.getTime();
    edm = endDate.getTime();    
    //    alert ('?r <http://weblifelog.com/ontology/atTime> ?time. FILTER(?time >= '+sdm+'). FILTER (?time <= "'+edm+'").');
    return '?r <http://weblifelog.com/ontology/atTime> ?time. FILTER(?time >= '+sdm+'). FILTER (?time <= '+edm+').';
}


function queryForEntities(type, limit, dcelement){
    goneCounter++;
    var formula = "count(distinct ?ws)"+
	"*AVG(?conf)"+
	//	"/AVG(?er)"+
	"";

    var squery = "select distinct ?x ?l ("+formula+" as ?nws) "+
	"where {?r <http://datami.co.uk/ontology/relatedTo> ?ea. "+
	typePattern(type)+
	siteFilterPatterns()+
	entityFilterPatterns()+
	dateFilterPatterns()+
	"?ea <http://fise.iks-project.eu/ontology/entity-reference> ?x."+
	"?r <http://weblifelog.com/ontology/toSite> ?ws."+
	"?ea <http://fise.iks-project.eu/ontology/confidence> ?conf."+
	"?x <http://www.iks-project.eu/ontology/rick/model/entityRank> ?er."+
	"?ea <http://fise.iks-project.eu/ontology/entity-label> ?l"+
	"} group by ?x ?l order by desc(?nws) limit "+limit;    
    var sparqler = new SPARQL.Service("http://your.server.com:3030/datami/query");
    var query = sparqler.createQuery();
    query.query(squery, {failure: failed, success:
	    function(json){processEntityResults(json, dcelement)}});
}

function failed(){
    goneCounter--;
    alert("query failed");
}

function addToEntityFilter(dcelement, uri){
    // need to be able to change the color as well... needs a lot of hashcodes...
    entityFilters[entityFilters.length] = uri;
    updateTCs();
}

function removeFromEntityFilter(dcelement, uri){
    // need to be able to change the color as well... needs a lot of hashcodes...
    entityFilters.splice(entityFilters.indexOf(uri), 1);
    updateTCs();
}

var entitiesArray = new Array();
var sitesArray = new Array();

var goneCounter = 0;

function updateTCs(){
    entitiesArray = new Array();
    sitesArray = new Array();
    queryForEntities(selectedTypes[0], 25, "datamitags1");
    queryForEntities(selectedTypes[1], 25, "datamitags2");
    queryForEntities(selectedTypes[2], 25, "datamitags3");
    queryForWebsites(25, "datamitags4");
    queryForTypes();
    showLoadingDialog();
}

function showLoadingDialog(){
    if (goneCounter==0){
	document.getElementById("message").style.display="none";
	document.getElementById("message").innerHTML="Done...";
	setUpTooltips();
    } else {
	document.getElementById("message").style.display="block";
	document.getElementById("message").innerHTML="Loading... ("+goneCounter+")";
	setTimeout("showLoadingDialog()", 500);
    }
}


function setUpTooltips(){
    //    alert("setting up tooltips for "+entitiesArray.length+" entities");
    for (x in entitiesArray){
	$('#entity'+x).tooltip({
		delay: 250,
		    showURL: false,
		    outdelay: 2500,
		    track: false,
		    top: 0,
		    bodyHandler: function() {
		    return entityView($(this).attr("id").substring(6));
		}
	    });
    }
    for (x in sitesArray){
	$('#site'+x).tooltip({
		delay: 250,
		    showURL: false,
		    outdelay: 2500,
		    track: false,
		    top: 0,
		    bodyHandler: function() {
  		      return siteView($(this).attr("id").substring(4));
		}
	    });
    }
}

function entityView(x){
    $("#tooltip").html("Loading...");
    queryEntityView(x);
    return "Loading...";
}


function siteView(x){
    $("#tooltip").html("Loading site "+x);
    querySiteView(x);
    return "Loading...";
}


function queryEntityView(x){
    uri = entitiesArray[x];
    var squery = "select distinct ?l ?ws (count(distinct ?ea) as ?nb)"+
	"where {?r <http://datami.co.uk/ontology/relatedTo> ?ea. "+
	"?ea <http://fise.iks-project.eu/ontology/entity-reference> <"+uri+">."+
	"?r <http://weblifelog.com/ontology/toSite> ?ws."+
	//	"?ea <http://fise.iks-project.eu/ontology/confidence> ?conf."+
	//	"?x <http://www.iks-project.eu/ontology/rick/model/entityRank> ?er."+
	"?ea <http://fise.iks-project.eu/ontology/entity-label> ?l"+
	"} group by ?ws ?l order by desc(?nb) limit 100";
    var sparqler = new SPARQL.Service("http://your.server.com:3030/datami/query");
    var query = sparqler.createQuery();
    query.query(squery, {failure: failed, success:
	    function(json){processEntityView(json, uri)}});
}

function querySiteView(x){
    uri = sitesArray[x];
    var squery = "select distinct ?x ?l (avg(distinct ?conf) as ?n)"+
	"where {?r <http://datami.co.uk/ontology/relatedTo> ?ea. "+
	"?ea <http://fise.iks-project.eu/ontology/entity-reference> ?x."+
	"?r <http://weblifelog.com/ontology/toSite> <"+uri+">."+
	"?ea <http://fise.iks-project.eu/ontology/confidence> ?conf."+
	//	"?x <http://www.iks-project.eu/ontology/rick/model/entityRank> ?er."+
	"?ea <http://fise.iks-project.eu/ontology/entity-label> ?l"+
	"} group by ?x ?l order by desc(?n) limit 100";
    var sparqler = new SPARQL.Service("http://your.server.com:3030/datami/query");
    var query = sparqler.createQuery();
    query.query(squery, {failure: failed, success:
	    function(json){processSiteView(json, uri)}});
}


function processEntityView(json, uri){
    var entityViewText = "";
    if (json['results']['bindings'][0]['l']) 
	wikipediaURL = "http://en.wikipedia.org/wiki/"+uri.substring(uri.lastIndexOf("/")+1);
	entityViewText +='<h2><a target="_blank" href="'+wikipediaURL+'">'+json['results']['bindings'][0]['l']['value']+'</a>'+	    
	    "</h2>"+
	    "Encountered in:<br/>";
    for (x in json['results']['bindings']){
	entityViewText += '<a target="_blank" href="'+json['results']['bindings'][x]['ws']['value']+'">'+json['results']['bindings'][x]['ws']['value']+"</a> ("+json['results']['bindings'][x]['nb']['value']+" times)<br/>";
    }
    $("#tooltip").html(entityViewText);
}

function processSiteView(json, uri){
    var siteViewText = '<stong><a target="_blank" href="'+uri+'">'+uri+'</a><hr/>';    
    for (x in json['results']['bindings']){
	if (json['results']['bindings'][x]['x'])
	    siteViewText += '<a target="_blank" href="'+json['results']['bindings'][x]['x']['value']+'">'+json['results']['bindings'][x]['l']['value']+"</a> (confidence: "+json['results']['bindings'][x]['n']['value']+")<br/>";
    }
    $("#tooltip").html(siteViewText);
}


function processEntityResults(json, dcelement){
    goneCounter--;
    var list = "<ul>";
    var max = 0;
    for (x in json['results']['bindings']){
	if (json['results']['bindings'][x]['nws'])
	    if (Number(json['results']['bindings'][x]['nws']['value']) > max) max = Number(json['results']['bindings'][x]['nws']['value']);	
    }
    if (json['results']['bindings'][0]['x'])
	for (x in json['results']['bindings']){
	    nws = Number(json['results']['bindings'][x]['nws']['value']);
	    nb = Math.round(((nws-1)/(max/5))+0.5);
	    ent = json['results']['bindings'][x]['x']['value'];
	    indOf = entitiesArray.indexOf(ent);
	    if (indOf==-1){
		indOf = entitiesArray.length;
		entitiesArray[indOf] = ent;
	    }
	    if (entityFilters.indexOf(json['results']['bindings'][x]['x']['value'])!=-1){
		list += '<li class="tag'+nb+'"><span ID="entity'+indOf+'"> <a style="color: red" href="javascript:removeFromEntityFilter(\''+dcelement+'\',\''+json['results']['bindings'][x]['x']['value']+'\');">'+json['results']['bindings'][x]['l']['value']+'</a></span> </li>';
	    }
	    else {
		list += '<li class="tag'+nb+'"><span ID="entity'+indOf+'"> <a  href="javascript:addToEntityFilter(\''+dcelement+'\',\''+json['results']['bindings'][x]['x']['value']+'\');">'+json['results']['bindings'][x]['l']['value']+'</a></span> </li>';
	    }
	}
    list += "</ul>";
    $("#"+dcelement).html(list);
    $("#"+dcelement+" ul li").tsort({order:"asc"});
    //  var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
    //  function(){
    //  $("#"+dcelement+" ul li").tsort({order:"desc",attr:"class"});
    //  $("#"+dcelement+" ul").hide().addClass("alt").fadeIn("fast");
    //  },
    //  function(){
    //      $("#"+dcelement+" ul li").tsort({order:"asc"});
    //  $("#"+dcelement+" ul").hide().removeClass("alt").fadeIn("fast");
    //  }
    //  );
    //  $('#'+dcelement).append(switcher);   
}


function typePattern(type){
    return "?ea <http://fise.iks-project.eu/ontology/entity-type> <"+type+">.";
}

function queryForWebsites(limit, dcelement){
    goneCounter++;
    var formula = "count(distinct ?x)";
    var squery = "select distinct ?ws ("+formula+" as ?nws) "+
	"where {?r <http://datami.co.uk/ontology/relatedTo> ?ea. "+
	"?ea <http://fise.iks-project.eu/ontology/entity-reference> ?x."+
	entityFilterPatterns()+
	dateFilterPatterns()+
	"?r <http://weblifelog.com/ontology/toSite> ?ws."+
	"} group by ?ws order by desc(?nws) limit "+limit;    
    var sparqler = new SPARQL.Service("http://your.server.com:3030/datami/query");
    var query = sparqler.createQuery();
    query.query(squery, {failure: failed, success:
	    function(json){processWebsiteResults(json, dcelement)}});
}

function removeFromWebsiteFilter(uri){
    siteFilters.splice(entityFilters.indexOf(uri), 1);
    updateTCs();
}

function addToWebsiteFilter(url){
    // need to be able to change the color as well... needs a lot of hashcodes...
    siteFilters[siteFilters.length] = url;
    updateTCs();
}

function processWebsiteResults(json, dcelement){
    goneCounter--;
    var list = "<ul>";
    var max = 0;
    for (x in json['results']['bindings']){
	if (Number(json['results']['bindings'][x]['nws']['value']) > max) max = Number(json['results']['bindings'][x]['nws']['value']);	
    }
    for (x in json['results']['bindings']){
	nws = Number(json['results']['bindings'][x]['nws']['value']);
	nb = Math.round(((nws-1)/(max/5))+0.5);
	ws = json['results']['bindings'][x]['ws']['value'];
	indOf = sitesArray.indexOf(ws);
	if (indOf==-1){
	    indOf = sitesArray.length;
	    sitesArray[indOf] = ws;
	}
	if (siteFilters.indexOf(json['results']['bindings'][x]['ws']['value'])!=-1){
	    list += '<li class="tag'+nb+'"><span ID="site'+indOf+'"><a style="color: red" href="javascript:removeFromWebsiteFilter(\''+json['results']['bindings'][x]['ws']['value']+'\');">'+domainOf(json['results']['bindings'][x]['ws']['value'])+'</a> <span></li>';
	} else {	   
	    list += '<li class="tag'+nb+'"><span ID="site'+indOf+'"><a href="javascript:addToWebsiteFilter(\''+json['results']['bindings'][x]['ws']['value']+'\');">'+domainOf(json['results']['bindings'][x]['ws']['value'])+'</a> </li>';
	}
    }
    list += "</ul>";
    $("#"+dcelement).html(list);
    $("#"+dcelement+" ul li").tsort({order:"asc"});
    //    var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
    //									   function(){
    //									       $("#"+dcelement+" ul li").tsort({order:"desc",attr:"class"});
    //									       $("#"+dcelement+" ul").hide().addClass("alt").fadeIn("fast");
    //									   },
    //									   function(){
    //									       $("#"+dcelement+" ul li").tsort({order:"asc"});
    //									       $("#"+dcelement+" ul").hide().removeClass("alt").fadeIn("fast");
    //									   });
    //    $('#'+dcelement).append(switcher);   
}

function domainOf(url){
    r= url.substring(url.indexOf("//")+2);
    r= r.substring(0,r.indexOf("/"));
    return r;
}


var types = new Array();

function queryForTypes(){
    var squery = "select distinct ?t (count(distinct ?x) as ?n)"+
	"where {"+
	"?r <http://datami.co.uk/ontology/relatedTo> ?x. "+
	siteFilterPatterns()+
	entityFilterPatterns()+	
	dateFilterPatterns()+
	"?x <http://fise.iks-project.eu/ontology/entity-type> ?t"+	
	"} group by ?t order by desc(?n)";
    var sparqler = new SPARQL.Service("http://your.server.com:3030/datami/query");
    var query = sparqler.createQuery();
    query.query(squery, {failure: failed, success:
	    function(json){processTypes(json)}});
}

function processTypes(json){
    types = new Array();
    count = 0;
    for (x in json['results']['bindings']){
	if (Number(json['results']['bindings'][x]['n']['value'])>2) types[count++] = json['results']['bindings'][x]['t']['value'];
    }
}

function getTypes(typenb){
    $("#tooltip").html("Loading...");
    var results = "";
    if (types.length==0){
	for (i = 0; i < 50; i++){
	    results +="<span></span><br/>";
	}
    }
    column2 = false;
    results+="<table>";
    for (x in types){
	if (types[x].indexOf("http://dbpedia.org")==0){
	    if (!column2) results+= "<tr>";
	    results += '<td class="type"><a href="javascript:selectForType('+typenb+', \''+types[x]+'\');">'+types[x].substring(types[x].lastIndexOf("/")+1)+'</a></span></td>';
	    if (column2) {results += '</tr>';}
	    column2 = !column2;
	}
    }
    results+="</table>";
    $("#tooltip").html(results);
    return results;
}
