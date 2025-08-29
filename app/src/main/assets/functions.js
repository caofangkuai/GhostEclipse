addFunction('Music', 'MusicOpen', 'MusicClose', JSON.stringify([{
	"type": "selector",
	"text": "Song:",
	"options": ["你看到的我(DJ版)", "Gold Dust(Radio Edit)"],
	"defaultOption": 0,
	"onChange": "MusicOnChange"
}]));
addFunction('Watermark', 'WatermarkOpen', 'WatermarkClose');