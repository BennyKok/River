var bot = new RiveScript({utf8: true});

function getRsReply(code,user,input) {
  bot.stream(code);
  bot.sortReplies();
  return bot.reply(user,input);
//  bot.reply(user,input).then(function(reply) {
//    return reply;
//  });
}