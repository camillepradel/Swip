function Desc(descriptiveSentence)
{
	this.descriptiveSentence = descriptiveSentence;
	this.generalizedSentence = this.descriptiveSentence.string;

	this.getGeneralizedSentence = function()
	{
		return this.generalizedSentence;
	}

	this.getGenIds = function()
	{
		return this.descriptiveSentence.string.match(/_gen\d+_/g);
	}

	this.getGen = function(genId)
	{
		return this.descriptiveSentence.gen[genId];
	}

	var gens = this.getGenIds();

	for(var i = 0; i < gens.length; i++)
	{
		var genId = gens[i].substr(4, 1);

		var ul = '<select><option>' + this.descriptiveSentence.gen[genId].join('</option><option>') + '</option></select>';
		var reg = new RegExp('_gen' + genId + '_');
		this.generalizedSentence = this.generalizedSentence.replace(reg, ul);
	}
}