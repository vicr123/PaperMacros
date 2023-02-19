import React from 'react';
import ReactDOM from 'react-dom/client';
import Styles from "./macroEditor.module.css";

let editorRoot;

class EditorPart extends React.Component {
    renderEditPart() {
        switch (this.props.step.type) {
            case "command":
                return <input className={Styles.editorPartEditor} type={"text"} value={this.props.step.command} onChange={this.commandChanged.bind(this)} />
            case "delay":
                return <div className={Styles.editorPartEditor}>
                    <input type={"number"} value={this.props.step.delay} onChange={this.delayChanged.bind(this)} min={1} />
                    <span>ticks</span>
                </div>
        }
    }

    renderActionButtons() {
        return <>
            <button onClick={() => this.props.onMoveUp(this.props.index)} style={{visibility: this.props.index === 0 ? "hidden" : "visible"}}>⮝</button>
            <button onClick={() => this.props.onMoveDown(this.props.index)} style={{visibility: this.props.index === this.props.count - 1 ? "hidden" : "visible"}}>⮟</button>
            <button onClick={() => this.props.onRemove(this.props.index)}>&times;</button>
        </>
    }

    commandChanged(e) {
        this.props.onUpdate(this.props.index, {
            type: "command",
            command: e.target.value
        })
    }

    delayChanged(e) {
        this.props.onUpdate(this.props.index, {
            type: "delay",
            delay: e.target.value
        })
    }

    typeChanged(e) {
        let value = e.target.value;
        if (this.props.step.type === value) return;

        switch (value) {
            case "command":
                this.props.onUpdate(this.props.index, {
                    type: "command",
                    command: "ping"
                })
                break;
            case "delay":
                this.props.onUpdate(this.props.index, {
                    type: "delay",
                    delay: 20
                })
                break;
        }
    }

    render() {
        return <div className={Styles.editorPart}>
            <select onChange={this.typeChanged.bind(this)} value={this.props.step.type}>
                <option value={"command"}>Command</option>
                <option value={"delay"}>Delay</option>
            </select>
            {this.renderEditPart()}
            {this.renderActionButtons()}
        </div>
    }
}

class MacroEditor extends React.Component {
    constructor(props) {
        super(props);

        let existingMacro = props.macro || {
            name: "New Macro",
            shared: false,
            macro: "[]"
        };

        this.state = {
            id: existingMacro.id,
            name: existingMacro.name,
            shared: existingMacro.shared,
            macro: JSON.parse(existingMacro.macro)
        }
    }

    static mount(editor) {
        editorRoot = ReactDOM.createRoot(document.getElementById("editor"));
        editorRoot.render(editor);
    }

    static unmount() {
        editorRoot.unmount();
    }

    newMacroPart() {
        this.setState(state => {
            let steps = state.macro;

            steps.push({
                type: "command",
                command: "ping"
            })

            return {
                macro: steps
            };
        })
    }

    updatePart(i, part) {
        this.setState(state => {
            let steps = state.macro;
            steps[i] = part;
            return {
                macro: steps
            };
        })
    }

    movePartUp(i) {
        this.setState(state => {
            let steps = state.macro;

            [steps[i], steps[i - 1]] = [steps[i - 1], steps[i]];

            return {
                macro: steps
            };
        })
    }

    movePartDown(i) {
        this.setState(state => {
            let steps = state.macro;

            [steps[i], steps[i + 1]] = [steps[i + 1], steps[i]];

            return {
                macro: steps
            };
        })
    }

    removePart(i) {
        this.setState(state => {
            let steps = state.macro;
            steps.splice(i, 1);
            return {
                macro: steps
            };
        })
    }

    async save() {
        await this.props.onSave({
            id: this.state.id,
            name: this.state.name,
            shared: this.state.shared,
            macro: JSON.stringify(this.state.macro)
        });
        MacroEditor.unmount();
    }

    nameChanged(e) {
        this.setState({
            name: e.target.value
        });
    }

    shareChanged(e) {
        this.setState({
            shared: e.target.checked
        })
    }

    render() {
        return <div className={Styles.scrim}>
                <div className={Styles.editor}>
                    <span className={Styles.nameLabel}>Name</span>
                    <input type={"text"} className={Styles.nameBox} value={this.state.name} onChange={this.nameChanged.bind(this)}/>

                    <span className={Styles.shareLabel}>Share</span>
                    <input type={"checkbox"} className={Styles.shareBox} checked={this.state.shared} onChange={this.shareChanged.bind(this)}/>

                    <div className={Styles.macroBox}>
                        {this.state.macro.map((step, i) => <EditorPart key={i} index={i} count={this.state.macro.length} step={step} onUpdate={this.updatePart.bind(this)} onMoveUp={this.movePartUp.bind(this)} onMoveDown={this.movePartDown.bind(this)} onRemove={this.removePart.bind(this)} />)}
                        <button className={Styles.addButton} onClick={this.newMacroPart.bind(this)}>Add Step</button>
                    </div>

                    <div className={Styles.buttonBox}>
                        <button onClick={MacroEditor.unmount}>Discard Changes</button>
                        <button onClick={this.save.bind(this)}>Save</button>
                    </div>
                </div>
            </div>
    }
}

export default MacroEditor;