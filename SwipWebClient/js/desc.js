function Desc(descriptiveSentence, mappingId)
{
	this.descriptiveSentence = descriptiveSentence;
	this.generalizedSentence = this.descriptiveSentence.string;
	this.covered = [mappingId];
	this.maxDescent = 0;

	this.getGeneralizedSentence = function(descA)
	{
		this.generateGeneralizedSentence(descA);
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

	this.addCovered = function(ar)
	{
		for(var i = 0; i < ar.length; i++)
		{
			if($.inArray(ar[i], this.covered) < 0)
				this.covered.push(ar[i]);
		}
	}

	this.getCovered = function()
	{
		return this.covered;
	}

	this.getMaxDescent = function()
	{
		return this.maxDescent;
	}

	this.generateGeneralizedSentence = function(descA)
	{
		var gens = this.getGenIds();

		if(gens != null)
		{
			for(var i = 0; i < gens.length; i++)
			{
				var j = 0;
				var cont = true;
				var genId = gens[i].substr(4, 1);

				this.covered = this.covered.sort(function(a, b) { return a - b});

				while(j < this.covered.length && cont)
				{
					var covGens = descA[this.covered[j]].getGenIds();

					if(covGens != null && $.inArray(gens[i], covGens) >= 0)
					{
						cont = false;
						if(this.descriptiveSentence.gen[genId][0] != descA[this.covered[j]].descriptiveSentence.gen[genId][0] && j > this.maxDescent)
							this.maxDescent = j;
					}
					else
						j++;
				}

				var bestMapping = descA[this.covered[j]].descriptiveSentence.gen[genId][0];

				var ul = '<select id="gen_mappingId__' + genId + '"><option>' + this.descriptiveSentence.gen[genId].join('</option><option>') + '</option></select>';
				var reg = new RegExp('_gen' + genId + '_');
				this.generalizedSentence = this.generalizedSentence.replace(reg, ul);

				this.generalizedSentence = this.generalizedSentence.replace('<option>' + bestMapping + '</option>', '<option selected="selected">' + bestMapping + '</option>');
				
				reg = new RegExp('_assoc'+genId+'_', "gm");
				this.generalizedSentence = this.generalizedSentence.replace(reg, '<span class="assoc_mappingId__'+genId+'">'+bestMapping+'</span>');
			}
		}
	}

	//this.generalizedSentence = this.generalizedSentence.replace(/,([^,]+)/g, '<span class="removable">,$1</span>');
}