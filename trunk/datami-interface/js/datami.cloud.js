
function dcInit(){
    initiateDatamiClouds();
    //    createSwitchLinks();
}


var selectedTypes = new Array(); 

selectedTypes[0]="http://dbpedia.org/ontology/Person";
selectedTypes[1]="http://dbpedia.org/ontology/Place";
selectedTypes[2]="http://dbpedia.org/ontology/Organisation";

function initiateDatamiClouds(){
    queryForTypes();
    $('#boxtitle1').tooltip({
	    delay: 250,
		showURL: false,
		track: false,
		outdelay: 6000,
		top: 0,
		bodyHandler: function() {
		return getTypes(0);
	    }
	});
    $('#boxtitle2').tooltip({
	    delay: 250,
		showURL: false,
		track: false,
		outdelay: 6000,
		top: 0,
		bodyHandler: function() {
		return getTypes(1);
	    }
	});
    $('#boxtitle3').tooltip({
	    delay: 250,
		showURL: false,
		track: false,
		outdelay: 6000,
		top: 0,
		bodyHandler: function() {
		return getTypes(2);
	    }
	});
    updateTCs();
}

function selectForType(typenb, type){
    selectedTypes[typenb] = type;
    $("#boxtitle"+(typenb+1)).html("<h2>"+type.substring(type.lastIndexOf("/")+1)+"</h2>");
    queryForEntities(selectedTypes[typenb], 25, "datamitags"+(typenb+1));
    showLoadingDialog();
}


function createSwitchLinks(){
  $("#datamitags1 ul li").tsort({order:"asc"});
  var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
  function(){
  $("#datamitags1 ul li").tsort({order:"desc",attr:"class"});
  $("#datamitags1 ul").hide().addClass("alt").fadeIn("fast");
  },
  function(){
  $("#datamitags1 ul li").tsort({order:"asc"});
  $("#datamitags1 ul").hide().removeClass("alt").fadeIn("fast");
  }
  );
  $('#datamitags1').append(switcher);   


  $("#datamitags2 ul li").tsort({order:"asc"});
  var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
  function(){
  $("#datamitags2 ul li").tsort({order:"desc",attr:"class"});
  $("#datamitags2 ul").hide().addClass("alt").fadeIn("fast");
  },
  function(){
  $("#datamitags2 ul li").tsort({order:"asc"});
  $("#datamitags2 ul").hide().removeClass("alt").fadeIn("fast");
  }
  );
  $('#datamitags2').append(switcher);   


  $("#datamitags3 ul li").tsort({order:"asc"});
  var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
  function(){
  $("#datamitags3 ul li").tsort({order:"desc",attr:"class"});
  $("#datamitags3 ul").hide().addClass("alt").fadeIn("fast");
  },
  function(){
  $("#datamitags3 ul li").tsort({order:"asc"});
  $("#datamitags3 ul").hide().removeClass("alt").fadeIn("fast");
  }
  );
  $('#datamitags3').append(switcher);   


  $("#datamitags4 ul li").tsort({order:"asc"});
  var switcher = $('<a href="javascript:void(0)">List/Cloud</a>').toggle(
  function(){
  $("#datamitags4 ul li").tsort({order:"desc",attr:"class"});
  $("#datamitags4 ul").hide().addClass("alt").fadeIn("fast");
  },
  function(){
  $("#datamitags4 ul li").tsort({order:"asc"});
  $("#datamitags4 ul").hide().removeClass("alt").fadeIn("fast");
  }
  );
  $('#datamitags4').append(switcher);   
}