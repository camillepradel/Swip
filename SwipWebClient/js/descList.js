// Imports
// $.getScript('js/desc.js');

function DescList() 
{
	this.descA = [];

	this.add = function(descriptiveSentence)
	{
		this.descA.push(new Desc(descriptiveSentence));
	}

	this.checkCover = function()
	{
		var ret = [];

		for(var i = 0; i < this.descA.length; i++)
		{
			for(var j = 0; j < this.descA.length; j++)
			{
				if(i != j && $.inArray(j, ret) < 0)
				{
					var iCoversJ = true;

					iGen = this.descA[i].getGenIds();
					jGen = this.descA[j].getGenIds();

					if(iGen.length <= jGen.length)
						iCoversJ = false;
					else
					{
						for(var k = 0; k < jGen.length; k++)
						{
							if($.inArray(jGen[k], iGen) < 0)
								iCoversJ = false;
							k++;
						}
					}

					if(iCoversJ && $.inArray(j, ret) < 0)
						ret.push(j);
				}
			}
		}

		return ret;
	}

	this.getGeneralizedSentence = function(id)
	{
		return this.descA[id].getGeneralizedSentence();
	}
}