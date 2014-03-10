// for the time, simply copy / paste from all node modules
// in the future, integrate Browserify and something similar
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

var CommentList = React.createClass({
    render: function() {
        var commentsNodes = this.props.data.map(function(comment) {
           return Comment({author: comment.author, children: comment.text})
        });
        return (
            React.DOM.div({
                className: 'commentList',
                children: commentsNodes
            })
        );
    }
});

var CommentForm = React.createClass({
    handleSubmit: function() {
        var author = this.refs.author.getDOMNode().value.trim();
        var text = this.refs.text.getDOMNode().value.trim();
        if (!text || !author) {
            return false;
        }
        this.props.onCommentSubmit({author: author, text: text});
        this.refs.author.getDOMNode().value = '';
        this.refs.text.getDOMNode().value = '';
        return false;
    },
    render: function() {
        return (
            React.DOM.form({
                className: 'commentForm',
                onSubmit: this.handleSubmit,
                children: [
                    React.DOM.input({type: 'text', ref:'author', placeholder: 'Your name'}),
                    React.DOM.input({type: 'text', ref:'text', placeholder: 'Say something...'}),
                    React.DOM.input({type: 'submit', value: 'Post'})
                ]
            })
        );
    }
});

var CommentBox = function(backend) {
    return React.createClass({
        getInitialState: function() {
            return {data: this.props.data || []};
        },
        handleCommentSubmit: function(comment) {
            backend.handleCommentSubmit({
                data: JSON.stringify(comment),
                success: function(data) {
                    this.setState({data: data});
                }.bind(this)
            });
        },
        componentWillMount: function() {
            backend.loadCommentsFromServer({
                success: function(data) {
                    this.setState({data: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(this.props.url, status, err.toString());
                }.bind(this)
            });
        },
        render: function() {
            return (
                React.DOM.div({
                    className: 'commentBox',
                    children: [
                        React.DOM.h1({children: 'Comments'}),
                        CommentList({data: this.state.data}),
                        CommentForm({onCommentSubmit: this.handleCommentSubmit})
                    ]
                })
            );
        }
    });
};
