var React = require('./react');

var Comment = React.createClass({
    render: function() {
        return (
            React.DOM.div({
                className: 'commentAuthor',
                children: [
                    React.DOM.h1({className: 'commentAuthor', children: this.props.author}),
                    this.props.children
                ]
            })
        );
    }
});

module.exports = Comment;